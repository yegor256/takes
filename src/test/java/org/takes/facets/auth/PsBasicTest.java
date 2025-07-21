/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.xml.bind.DatatypeConverter;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.facets.forward.RsForward;
import org.takes.facets.forward.TkForward;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rq.RqWithHeader;
import org.takes.rq.RqWithHeaders;
import org.takes.rs.RsHeadPrint;
import org.takes.rs.RsPrint;
import org.takes.tk.TkText;

/**
 * Test of {@link PsBasic}.
 * @since 0.20
 */
final class PsBasicTest {

    /**
     * Basic Auth.
     */
    private static final String AUTH_BASIC = "Authorization: Basic %s";

    /**
     * Valid code parameter.
     */
    private static final String VALID_CODE = "?valid_code=%s";

    /**
     * Size for random symbol generator.
     */
    private static final int TEN = 10;

    @Test
    void handleConnectionWithValidCredential() throws Exception {
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
                        RandomStringUtils.randomAlphanumeric(PsBasicTest.TEN)
                    )
                ),
                PsBasicTest.header(user, "pass")
            )
        );
        MatcherAssert.assertThat(
            "Identity must be present for valid credentials",
            identity.has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    @Test
    void handleConnectionWithValidCredentialDefaultEntry()
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
                        RandomStringUtils.randomAlphanumeric(PsBasicTest.TEN)
                    )
                ),
                PsBasicTest.header(user, password)
            )
        );
        MatcherAssert.assertThat(
            "Identity must be present for valid credentials",
            identity.has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    @Test
    void handleConnectionWithInvalidCredential() throws Exception {
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
                            RandomStringUtils.randomAlphanumeric(PsBasicTest.TEN)
                        )
                    ),
                    PsBasicTest.header("username", "wrong")
                )
            );
        } catch (final RsForward ex) {
            forward = ex;
        }
        MatcherAssert.assertThat(
            new RsHeadPrint(forward).asString(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 401 Unauthorized"),
                Matchers.containsString(
                    "WWW-Authenticate: Basic ream=\"RealmB\""
                )
            )
        );
    }

    @Test
    void handleMultipleHeadersWithValidCredential() throws Exception {
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
                        RandomStringUtils.randomAlphanumeric(PsBasicTest.TEN)
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
        MatcherAssert.assertThat(
            "Identity must be present for valid credentials",
            identity.has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    @Test
    void handleMultipleHeadersWithInvalidContent() {
        Assertions.assertThrows(
            HttpException.class,
            () -> MatcherAssert.assertThat(
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
            )
        );
    }

    @Test
    void authenticatesUser() throws Exception {
        final Take take = new TkAuth(
            new TkSecure(
                new TkText("secured")
            ),
            new PsBasic(
                "myrealm",
                new PsBasic.Default("mike secret11 urn:users:michael")
            )
        );
        new Assertion<>(
            "PsBasic should authenticate mike",
            new RsPrint(
                take.act(
                    new RqWithHeader(
                        new RqFake(),
                        PsBasicTest.header("mike", "secret11")
                    )
                )
            ),
            new HasString("HTTP/1.1 200 OK")
        ).affirm();
    }

    @Test
    void requestAuthentication() throws Exception {
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
        new Assertion<>(
            "Response with 401 Unauthorized status",
            new RsPrint(
                take.act(new RqFake())
            ),
            new HasString("HTTP/1.1 401 Unauthorized\r\n")
        ).affirm();
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
