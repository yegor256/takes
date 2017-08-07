/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.facets.auth;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.signatures.SiHmac;
import org.takes.misc.Base64;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;
import org.takes.rs.RsJson;

/**
 * Pass with JSON Web Token (JWT).
 *
 * <p>
 * The class is immutable and thread-safe.
 *
 * @author Sven Windisch (sven.windisch@gmail.com)
 * @version $Id$
 * @since 1.4
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle AvoidDuplicateLiterals (500 lines)
 */
@EqualsAndHashCode
public final class PsToken implements Pass {

    /**
     * Signature algorithm.
     */
    private final SiHmac signature;

    /**
     * HTTP Header to read.
     */
    private final String header;

    /**
     * Max age of token, in seconds.
     */
    private final long age;

    /**
     * Ctor. This is equivalent to {@code PsToken(key, 3600)}, signing with 256
     * bit.
     *
     * @param key
     *  The secret key to sign with
     */
    public PsToken(final String key) {
        // @checkstyle MagicNumber (1 line)
        this(new SiHmac(key, SiHmac.HMAC256), 3600L);
    }

    /**
     * Ctor. This uses a 256-bit HMAC signature.
     *
     * @param key
     *  The secret key to sign with
     * @param seconds
     *  The life span of the token.
     */
    public PsToken(final String key, final long seconds) {
        this(new SiHmac(key, SiHmac.HMAC256), seconds);
    }

    /**
     * Ctor.
     *
     * @param sign
     *  A {@see Signature}.
     * @param seconds
     *  The life span of the token.
     */
    private PsToken(final SiHmac sign, final long seconds) {
        this.header = "Authorization";
        this.signature = sign;
        this.age = seconds;
    }

    @Override
    public Opt<Identity> enter(final Request req) throws IOException {
        // @checkstyle ExecutableStatementCount (100 lines)
        final Iterator<String> headers = new RqHeaders.Base(req)
            .header(this.header).iterator();
        Opt<Identity> user = new Opt.Empty<>();
        String head = "";
        while (headers.hasNext()) {
            final String nexthead = headers.next();
            if (nexthead.trim().startsWith("Bearer")) {
                head = nexthead;
                break;
            }
        }
        final String dot = "\\.";
        if (head.length() > 0) {
            final String jwt = head.trim().split(" ", 2)[1].trim();
            final byte[] jwtheader = jwt.split(dot)[0].getBytes();
            final byte[] jwtpayload = jwt.split(dot)[1].getBytes();
            final byte[] jwtsign = jwt.split(dot)[2].getBytes();
            final ByteBuffer tocheck = ByteBuffer.allocate(
                jwtheader.length + jwtheader.length + 1
            );
            tocheck.put(jwtheader).put(".".getBytes()).put(jwtpayload);
            final byte[] checked = this.signature.sign(tocheck.array());
            if (jwtsign.equals(checked)) {
                final JsonObject jsonv = Json.createReader(
                    new StringReader(
                        new String(jwtpayload)
                        )
                    )
                    .readObject();
                user = new Opt.Single<Identity>(
                    new Identity.Simple(
                        jsonv.getString(Jwt.SUBJECT, new String())
                    )
                );
            }
        }
        return user;
    }

    @Override
    public Response exit(final Response res,
        final Identity idt) throws IOException {
        final byte[] jwtheader = new Jose(this.signature.bitlength()).encode();
        final byte[] jwtpayload = new Jwt(idt, this.age).encode();
        final ByteBuffer tosign = ByteBuffer.allocate(
            jwtheader.length + jwtpayload.length + 1
        );
        tosign.put(jwtheader);
        tosign.put(".".getBytes());
        tosign.put(jwtpayload);
        final byte[] sign = this.signature.sign(tosign.array());
        final JsonReader reader = Json.createReader(res.body());
        final JsonObject target = Json.createObjectBuilder()
            .add("response", reader.read())
            .add(
                "jwto", String.format(
                    "%s.%s.%s",
                    new String(jwtheader),
                    new String(jwtpayload),
                    new String(sign)
                )
            )
            .build();
        return new RsJson(
            target
        );
    }

    /**
     * JSON Object Signing and Encryption Header.
     */
    final class Jose {
        /**
         * JOSE object.
         */
        private final JsonObject joseo;

        /**
         * JSON Object Signing and Encryption Header.
         * @param bitlength Number of bits to use for encryption.
         */
        Jose(final int bitlength) {
            this.joseo = Json.createObjectBuilder()
                .add("alg", String.format("HS%s", bitlength))
                .add("typ", "JWT")
                .build();
        }

        /**
         * JSON output.
         * @return The JOSE header as JSON.
         */
        public JsonObject json() {
            return this.joseo;
        }

        /**
         * Base64 encoded JSON output.
         * @return The JOSE header as JSON, Base64-encoded.
         * @throws IOException If sth. goes wrong.
         */
        public byte[] encode() throws IOException {
            return new Base64().encode(this.joseo.toString());
        }
    }

    /**
     * JSON Web Token.
     */
    final class Jwt {
        /**
         * The header short for subject.
         */
        public static final String SUBJECT = "sub";

        /**
         * JWT object.
         */
        private final JsonObject jwto;

        /**
         * JSON Web Token.
         * Calling this is equivalent to {@code Jwt(idt, 3600)}.
         * @param idt The identity to provide.
         */
        Jwt(final Identity idt) {
            // @checkstyle MagicNumber (1 line)
            this(idt, 3600L);
        }

        /**
         * JSON Web Token.
         * @param idt The identity to provide.
         * @param age Lifetime of the token (in seconds).
         */
        Jwt(final Identity idt, final long age) {
            this.jwto = Json.createObjectBuilder()
                .add(
                    "exp", DateFormat.getDateInstance()
                        .format(
                            new Date(System.currentTimeMillis()
                                // @checkstyle MagicNumber (1 line)
                                + (age * 1000)
                            )
                        )
                    )
                .add(Jwt.SUBJECT, idt.urn())
                .build();
        }

        /**
         * JSON output.
         * @return The JWT as JSON.
         */
        public JsonObject json() {
            return this.jwto;
        }

        /**
         * Base64 encoded JSON output.
         * @return The JWT as JSON, Base64-encoded.
         * @throws IOException If sth. goes wrong.
         */
        public byte[] encode() throws IOException {
            return new Base64().encode(this.jwto.toString());
        }
    }
}
