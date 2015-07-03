/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;
import org.takes.facets.auth.PsBasic.User;
import org.takes.facets.forward.RsForward;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsPrint;

/**
 * Test of {@link PsBasic}.
 * @author Endrigo Antonini (teamed@endrigo.com.br)
 * @version $Id$
 * @since 0.20
 * @checkstyle ClassDataAbstractionCouplingCheck (300 lines)
 */
public final class PsBasicTest {

    /**
     * Basic Auth.
     */
    private static final String AUTH_BASIC = "Authorization: Basic %s";

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
                        "?valid_code=%s",
                        // @checkstyle MagicNumberCheck (1 line)
                        RandomStringUtils.randomAlphanumeric(10)
                    )
                ),
                this.generateAuthenticateHead(user, "pass")
            )
        );
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.generateIdentityUrn(user))
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
                            // @checkstyle MagicNumberCheck (1 line)
                            RandomStringUtils.randomAlphanumeric(10)
                        )
                    ),
                    this.generateAuthenticateHead("username", "wrong")
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
                        // @checkstyle MagicNumberCheck (1 line)
                        RandomStringUtils.randomAlphanumeric(10)
                    )
                ),
                this.generateAuthenticateHead(user, "changeit"),
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
            CoreMatchers.equalTo(PsBasicTest.generateIdentityUrn(user))
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
                        this.generateAuthenticateHead("user", "password")
                    ),
                    "XYZReferer: http://teamed.io/",
                    "XYZConnection:keep-alive",
                    "XYZContent-Encoding:gzip",
                    "XYZX-Check-Cacheable:YES",
                    "XYZX-Powered-By:Java/1.7"
                )
            ).has()
            , Matchers.is(false)
        );
    }

    /**
     * Test {@link org.takes.facets.auth.PsBasic.Default#enter} retunrs
     * a valid user.
     * With user and pass ok.
     */
    @Test
    public void returnsCorrectUrn() {
        final String user = "Admin";
        final String pass = "123";
        final String urn = "urn:basic:admin";
        final List<User> usrs =
            new ArrayList<User>(1);
        usrs.add(new User(user, pass, urn));
        final Opt<Identity> identity = new PsBasic.Default(usrs)
            .enter(user, pass);
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(urn)
        );
    }

    /**
     * Test {@link org.takes.facets.auth.PsBasic.Default#enter}.
     * With user ok and pass wrong returns empty identity.
     */
    @Test
    public void returnsFalseIfUserDoesNotExist() {
        final Opt<Identity> identity =
            new PsBasic.Default("Adm,123,urn:basic:adm").enter("Adm", "125");
        MatcherAssert.assertThat(identity.has(), Matchers.is(false));
    }

    /**
     * Test {@link org.takes.facets.auth.PsBasic.Default#enter}.
     * With user wrong and pass ok returns empty indentity.
     */
    @Test
    public void returnsFalseForWrongPassword() {
        final String pwd = "126";
        final List<User> usrs =
                new ArrayList<User>(1);
        usrs.add(new User("Adminis", pwd, "urn:basic;admins"));
        final Opt<Identity> identity = new PsBasic.Default(usrs)
            .enter("Administrator", pwd);
        MatcherAssert.assertThat(identity.has(), Matchers.is(false));
    }

    /**
     * Generate the identity urn.
     * @param user User
     * @return URN
     */
    private static String generateIdentityUrn(final String user) {
        return String.format("urn:basic:%s", user);
    }
    /**
     * Generate the string used on the request that store information about
     * authentication.
     * @param user Username
     * @param pass Password
     * @return Header string.
     */
    private String generateAuthenticateHead(
        final String user,
        final String pass
    ) {
        final String auth = String.format("%s:%s", user, pass);
        final String encoded = DatatypeConverter.printBase64Binary(
            auth.getBytes()
        );
        return String.format(AUTH_BASIC, encoded);
    }
}
