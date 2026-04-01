/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import jakarta.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.HttpException;
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
@SuppressWarnings("PMD.TooManyMethods")
final class PsBasicTest {

    /**
     * Basic Auth.
     */
    private static final String AUTH_BASIC = "Authorization: Basic %s";

    /**
     * Valid code parameter.
     */
    private static final String VALID_CODE = "?valid_code=%s";

    @Test
    void identityPresentForValidCredential() throws IOException {
        MatcherAssert.assertThat(
            "Identity must be present for valid credentials",
            PsBasicTest.enterWithFake("john", "pass").has(),
            Matchers.is(true)
        );
    }

    @Test
    void identityUrnMatchesForValidCredential() throws IOException {
        final String user = "john";
        MatcherAssert.assertThat(
            "Identity URN must match expected user URN for valid credentials",
            PsBasicTest.enterWithFake(user, "pass").get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    @Test
    void identityPresentForDefaultEntry() throws Exception {
        MatcherAssert.assertThat(
            "Identity must be present for valid credentials",
            PsBasicTest.enterWithDefault("johny", "password2").has(),
            Matchers.is(true)
        );
    }

    @Test
    void identityUrnMatchesForDefaultEntry() throws Exception {
        final String user = "johny";
        MatcherAssert.assertThat(
            "Identity URN must match expected user URN for default entry",
            PsBasicTest.enterWithDefault(user, "password2").get().urn(),
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
                            RandomStringUtils.randomAlphanumeric(10)
                        )
                    ),
                    PsBasicTest.header("username", "wrong")
                )
            );
        } catch (final RsForward ex) {
            forward = ex;
        }
        MatcherAssert.assertThat(
            "Invalid credentials must return 401 Unauthorized with WWW-Authenticate header",
            new RsHeadPrint(forward).asString(),
            Matchers.allOf(
                Matchers.containsString("HTTP/1.1 401 Unauthorized"),
                Matchers.containsString(
                    "WWW-Authenticate: Basic realm=\"RealmB\""
                )
            )
        );
    }

    @Test
    void identityPresentWithMultipleHeaders() throws Exception {
        MatcherAssert.assertThat(
            "Identity must be present for valid credentials",
            PsBasicTest.enterWithMultipleHeaders("bill", "changeit").has(),
            Matchers.is(true)
        );
    }

    @Test
    void identityUrnMatchesWithMultipleHeaders() throws Exception {
        final String user = "bill";
        MatcherAssert.assertThat(
            "Identity URN must match expected user URN with multiple headers",
            PsBasicTest.enterWithMultipleHeaders(user, "changeit").get().urn(),
            CoreMatchers.equalTo(PsBasicTest.urn(user))
        );
    }

    @Test
    void handleMultipleHeadersWithInvalidContent() {
        Assertions.assertThrows(
            HttpException.class,
            () -> MatcherAssert.assertThat(
                "Invalid headers must not provide identity",
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
        MatcherAssert.assertThat(
            "PsBasic should authenticate mike",
            new RsPrint(
                new TkAuth(
                    new TkSecure(
                        new TkText("secured")
                    ),
                    new PsBasic(
                        "myrealm",
                        new PsBasic.Default("mike secret11 urn:users:michael")
                    )
                ).act(
                    new RqWithHeader(
                        new RqFake(),
                        PsBasicTest.header("mike", "secret11")
                    )
                )
            ),
            new HasString("HTTP/1.1 200 OK")
        );
    }

    @Test
    void requestAuthentication() throws Exception {
        MatcherAssert.assertThat(
            "Response with 401 Unauthorized status",
            new RsPrint(
                new TkForward(
                    new TkAuth(
                        new TkSecure(
                            new TkText("secured area...")
                        ),
                        new PsBasic(
                            "the realm 5",
                            new PsBasic.Default("bob pwd88 urn:users:bob")
                        )
                    )
                ).act(new RqFake())
            ),
            new HasString("HTTP/1.1 401 Unauthorized\r\n")
        );
    }

    private static Opt<Identity> enterWithFake(
        final String user,
        final String pass
    ) throws IOException {
        return new PsBasic("RealmA", new PsBasic.Fake(true)).enter(
            new RqWithHeaders(
                new RqFake(
                    RqMethod.GET,
                    String.format(
                        PsBasicTest.VALID_CODE,
                        RandomStringUtils.randomAlphanumeric(10)
                    )
                ),
                PsBasicTest.header(user, pass)
            )
        );
    }

    private static Opt<Identity> enterWithDefault(
        final String user,
        final String pass
    ) throws IOException {
        return new PsBasic(
            "RealmAA",
            new PsBasic.Default(
                "mike my%20password1 urn:basic:michael",
                String.format("%s %s urn:basic:%s", user, pass, user)
            )
        ).enter(
            new RqWithHeaders(
                new RqFake(
                    RqMethod.GET,
                    String.format(
                        PsBasicTest.VALID_CODE,
                        RandomStringUtils.randomAlphanumeric(10)
                    )
                ),
                PsBasicTest.header(user, pass)
            )
        );
    }

    private static Opt<Identity> enterWithMultipleHeaders(
        final String user,
        final String pass
    ) throws IOException {
        return new PsBasic("RealmC", new PsBasic.Fake(true)).enter(
            new RqWithHeaders(
                new RqFake(
                    RqMethod.GET,
                    String.format(
                        "?multiple_code=%s",
                        RandomStringUtils.randomAlphanumeric(10)
                    )
                ),
                PsBasicTest.header(user, pass),
                "Referer: http://teamed.io/",
                "Connection:keep-alive",
                "Content-Encoding:gzip",
                "X-Check-Cacheable:YES",
                "X-Powered-By:Java/1.7"
            )
        );
    }

    private static String urn(final String user) {
        return String.format("urn:basic:%s", user);
    }

    private static String header(final String user, final String pass) {
        return String.format(
            PsBasicTest.AUTH_BASIC,
            DatatypeConverter.printBase64Binary(
                String.format("%s:%s", user, pass)
                    .getBytes(StandardCharsets.UTF_8)
            )
        );
    }
}
