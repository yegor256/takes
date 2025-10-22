/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import lombok.EqualsAndHashCode;
import org.cactoos.iterable.Mapped;
import org.cactoos.scalar.Constant;
import org.cactoos.scalar.FirstOf;
import org.cactoos.scalar.Not;
import org.cactoos.scalar.Unchecked;
import org.cactoos.text.IsBlank;
import org.cactoos.text.StartsWith;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.signatures.SiHmac;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;
import org.takes.rs.RsJson;

/**
 * Pass that authenticates users using JSON Web Token (JWT).
 * This implementation validates JWT tokens from the Authorization header,
 * verifying the signature and extracting the user identity from the payload.
 * It supports token generation and validation using HMAC signatures.
 *
 * <p>
 * The class is immutable and thread-safe.
 *
 * @since 1.4
 * @checkstyle ExecutableStatementCountCheck (500 lines)
 */
@EqualsAndHashCode
public final class PsToken implements Pass {

    /**
     * HMAC signature algorithm for signing and verifying tokens.
     */
    private final SiHmac signature;

    /**
     * Name of the HTTP header containing the JWT token.
     */
    private final String header;

    /**
     * Maximum age of the token before expiration, in seconds.
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
        final Opt<Identity> user = new Opt.Empty<>();
        final UncheckedText head = new Unchecked<>(
            new FirstOf<>(
                text -> new StartsWith(
                    new Trimmed(text),
                    new TextOf("Bearer")
                ).value(),
                new Mapped<>(
                    UncheckedText::new,
                    new RqHeaders.Base(req).header(this.header)
                ),
                new Constant<>(new UncheckedText(""))
            )
        ).value();
        if (new Unchecked<>(new Not(new IsBlank(head))).value()) {
            final String jwt = new UncheckedText(
                new Trimmed(new TextOf(head.asString().split(" ", 2)[1]))
            ).asString();
            final String[] parts = jwt.split("\\.");
            final byte[] jwtheader = parts[0].getBytes(
                Charset.defaultCharset()
            );
            final byte[] jwtpayload = parts[1].getBytes(
                Charset.defaultCharset()
            );
            final byte[] jwtsign = parts[2].getBytes(Charset.defaultCharset());
            final ByteBuffer tocheck = ByteBuffer.allocate(
                jwtheader.length + jwtpayload.length + 1
            );
            tocheck.put(jwtheader).put(".".getBytes(Charset.defaultCharset()))
                .put(jwtpayload);
            final byte[] checked = this.signature.sign(tocheck.array());
            if (Arrays.equals(jwtsign, checked)) {
                try (JsonReader rdr = Json.createReader(
                    new StringReader(
                        new String(
                            Base64.getDecoder().decode(jwtpayload),
                            Charset.defaultCharset()
                        )
                    )
                )) {
                    return new Opt.Single<>(
                        new Identity.Simple(
                            rdr.readObject().getString(Token.Jwt.SUBJECT)
                        )
                    );
                }
            }
        }
        return user;
    }

    @Override
    public Response exit(final Response res,
        final Identity idt) throws Exception {
        final byte[] jwtheader = new Token.Jose(
            this.signature.bitlength()
        ).encoded();
        final byte[] jwtpayload = new Token.Jwt(idt, this.age).encoded();
        final ByteBuffer tosign = ByteBuffer.allocate(
            jwtheader.length + jwtpayload.length + 1
        );
        tosign.put(jwtheader);
        tosign.put(".".getBytes(Charset.defaultCharset()));
        tosign.put(jwtpayload);
        final byte[] sign = this.signature.sign(tosign.array());
        try (JsonReader reader = Json.createReader(res.body())) {
            final JsonObject target = Json.createObjectBuilder()
                .add("response", reader.read())
                .add(
                    "jwt", String.format(
                        "%s.%s.%s",
                        new String(jwtheader, Charset.defaultCharset()),
                        new String(jwtpayload, Charset.defaultCharset()),
                        new String(sign, Charset.defaultCharset())
                    )
                )
                .build();
            return new RsJson(target);
        }
    }
}
