/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import com.jcabi.aspects.Tv;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.facets.forward.RsForward;
import org.takes.facets.forward.TkForward;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;
import org.takes.tk.TkText;

/**
 * Test of {@link PsBasic}.
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class PsBasicTest {

    /**
     * Basic Auth.
     */
    private static final String AUTH_BASIC = "Authorization: Basic %s";

    /**
     * Valid code parameter.
     */
    private static final String VALID_CODE = "?valid_code=%s";

    /**
     * PsBasic can handle connection with valid credential.
     * @throws Exception if any error occurs
     */
    @Test
    public void handleConnectionWithValidCredential() throws Exception {
        final String user = "john";
        final Opt<Identity> identity = new PsBasic(
            "RealmA",
            new PsBasic.Fake(true)
        ).enter(
            new RqWithHeaders(
                new RqFake(
                    RqMethod.GET,
                    String.format(
                        PsBasicTest.VALID_CODE,
                        RandomStringUtils.randomAlphanumeric(Tv.TEN)
                    )
                ),
                PsBasicTest.header(user, "pass")
            )
        );
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    /**
     * PsBasic can handle connection with valid credential when Entry is
     * a instance of Default.
     * @throws Exception if any error occurs
     */
    @Test
    public void handleConnectionWithValidCredentialDefaultEntry()
        throws Exception {
        final String user = "johny";
        final String password = "password2";
        final Opt<Identity> identity = new PsBasic(
            "RealmAA",
            new PsBasic.Default(
                "mike my%20password1 urn:basic:michael",
                String.format("%s %s urn:basic:%s", user, password, user)
            )
        ).enter(
            new RqWithHeaders(
                new RqFake(
                    RqMethod.GET,
                    String.format(
                        PsBasicTest.VALID_CODE,
                        RandomStringUtils.randomAlphanumeric(Tv.TEN)
                    )
                ),
                PsBasicTest.header(user, password)
            )
        );
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    /**
     * PsBasic can handle connection with invalid credential.
     * @throws Exception If some problem inside
     */
    @Test
    public void handleConnectionWithInvalidCredential() throws Exception {
        RsForward forward = new RsForward();
        try {
            new PsBasic(
                "RealmB",
                new PsBasic.Empty()
            ).enter(
                new RqWithHeaders(
                    new RqFake(
                        RqMethod.GET,
                        String.format(
                            "?invalid_code=%s",
                            RandomStringUtils.randomAlphanumeric(Tv.TEN)
                        )
                    ),
                    PsBasicTest.header("username", "wrong")
                )
            );
        } catch (final RsForward ex) {
            forward = ex;
        }
        MatcherAssert.assertThat(
            new RsPrint(forward).printHead(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 401 Unauthorized"),
                Matchers.containsString(
                    "WWW-Authenticate: Basic ream=\"RealmB\""
                )
            )
        );
    }

    /**
     * PsBasic can handle multiple headers with valid credential.
     * @throws Exception If some problem inside
     */
    @Test
    public void handleMultipleHeadersWithValidCredential() throws Exception {
        final String user = "bill";
        final Opt<Identity> identity = new PsBasic(
            "RealmC",
            new PsBasic.Fake(true)
        ).enter(
            new RqWithHeaders(
                new RqFake(
                    RqMethod.GET,
                    String.format(
                        "?multiple_code=%s",
                        RandomStringUtils.randomAlphanumeric(Tv.TEN)
                    )
                ),
                PsBasicTest.header(user, "changeit"),
                "Referer: http://teamed.io/",
                "Connection:keep-alive",
                "Content-Encoding:gzip",
                "X-Check-Cacheable:YES",
                "X-Powered-By:Java/1.7"
            )
        );
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    /**
     * PsBasic can handle multiple headers with invalid content.
     * @throws Exception If some problem inside
     */
    @Test(expected = HttpException.class)
    public void handleMultipleHeadersWithInvalidContent() throws Exception {
        MatcherAssert.assertThat(
            new PsBasic(
                "RealmD",
                new PsBasic.Fake(true)
            ).enter(
                new RqWithHeaders(
                    new RqFake(
                        "XPTO",
                        "/wrong-url"
                    ),
                    String.format(
                        "XYZ%s",
                        PsBasicTest.header("user", "password")
                    ),
                    "XYZReferer: http://teamed.io/",
                    "XYZConnection:keep-alive",
                    "XYZContent-Encoding:gzip",
                    "XYZX-Check-Cacheable:YES",
                    "XYZX-Powered-By:Java/1.7"
                )
            ).has(),
            Matchers.is(false)
        );
    }

    /**
     * PsBasic can authenticate a user.
     * @throws Exception If some problem inside
     */
    @Test
    public void authenticatesUser() throws Exception {
        final Take take = new TkAuth(
            new TkSecure(
                new TkText("secured")
            ),
            new PsBasic(
                "myrealm",
                new PsBasic.Default("mike secret11 urn:users:michael")
            )
        );
        MatcherAssert.assertThat(
            new RsPrint(
                take.act(
                    new RqWithHeader(
                        new RqFake(),
                        PsBasicTest.header("mike", "secret11")
                    )
                )
            ).print(),
            Matchers.containsString("HTTP/1.1 200 OK")
        );
    }

    /**
     * PsBasic can request authentication.
     * @throws Exception If some problem inside
     */
    @Test
    public void requestAuthentication() throws Exception {
        final Take take = new TkForward(
            new TkAuth(
                new TkSecure(
                    new TkText("secured area...")
                ),
                new PsBasic(
                    "the realm 5",
                    new PsBasic.Default("bob pwd88 urn:users:bob")
                )
            )
        );
        MatcherAssert.assertThat(
            new RsPrint(
                take.act(new RqFake())
            ).print(),
            Matchers.containsString("HTTP/1.1 401 Unauthorized\r\n")
        );
    }

    /**
     * Generate the identity urn.
     * @param user User
     * @return URN
     */
    private static String urn(final String user) {
        return String.format("urn:basic:%s", user);
    }

    /**
     * Generate the string used on the request that store information about
     * authentication.
     * @param user Username
     * @param pass Password
     * @return Header string.
     */
    private static String header(final String user, final String pass) {
        final String auth = String.format("%s:%s", user, pass);
        final String encoded = DatatypeConverter.printBase64Binary(
            auth.getBytes()
        );
        return String.format(PsBasicTest.AUTH_BASIC, encoded);
    }
}
