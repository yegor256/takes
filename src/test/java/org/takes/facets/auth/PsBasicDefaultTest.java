/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link org.takes.facets.auth.PsBasicDefault}.
 * @since 0.22
 */
final class PsBasicDefaultTest {

    @Test
    void acceptsCorrectLoginPasswordPair() {
        MatcherAssert.assertThat(
            "PsBasicDefault must authenticate user with correct login/password and return URN",
            new PsBasicDefault(
                new String[]{
                    "bob qwe%20r%20ty%3A%2B urn:foo:robert",
                    "alice пароль urn:foo:alice",
                }
            ).enter(
                "bob",
                "qwe r ty:+"
            ).get().urn(),
            new IsEqual<>("urn:foo:robert")
        );
    }

    @Test
    void supportsUrlEncodedSpaces() {
        MatcherAssert.assertThat(
            "PsBasicDefault must support URL-encoded spaces in passwords",
            new PsBasicDefault(
                new String[]{
                    "yvonne hey%20you urn:foo:z",
                }
            ).enter(
                "yvonne",
                "hey you"
            ).has(),
            new IsEqual<>(true)
        );
    }

    @Test
    void supportsPlusEncodedSpaces() {
        MatcherAssert.assertThat(
            "PsBasicDefault must support plus-encoded spaces in passwords",
            new PsBasicDefault(
                new String[]{
                    "zak hey+me urn:foo:z",
                }
            ).enter(
                "zak",
                "hey me"
            ).has(),
            new IsEqual<>(true)
        );
    }

    @Test
    void supportsUsersWithSpacesInTheirNames() {
        MatcherAssert.assertThat(
            "PsBasicDefault must support users with spaces in their names",
            new PsBasicDefault(
                new String[]{
                    "abraham+lincoln qwer urn:foo:z",
                }
            ).enter(
                "abraham lincoln",
                "qwer"
            ).has(),
            new IsEqual<>(true)
        );
    }

    @Test
    void supportsUrlencodedUrns() throws Exception {
        final String urn = "urn:a100%25:one-two+";
        MatcherAssert.assertThat(
            "PsBasicDefault must support URL-encoded URNs and decode them correctly",
            new PsBasicDefault(
                new String[]{
                    String.format(
                        "login password %s",
                        URLEncoder.encode(urn, StandardCharsets.UTF_8)
                    ),
                }
            ).enter(
                "login",
                "password"
            ).get().urn(),
            new IsEqual<>(urn)
        );
    }

    @Test
    void rejectsIncorrectPassword() {
        MatcherAssert.assertThat(
            "PsBasicDefault must reject authentication with incorrect password",
            new PsBasicDefault(
                new String[]{
                    "charlie qwerty urn:foo:charlie",
                    "doreen 123 urn:foo:doreen",
                }
            ).enter("charlie", "wrongpassword").has(),
            new IsEqual<>(false)
        );
    }

    @Test
    void rejectsIncorrectLogin() {
        MatcherAssert.assertThat(
            "PsBasicDefault must reject authentication with incorrect login",
            new PsBasicDefault(
                new String[]{
                    "eddie qwerty urn:foo:eddie",
                    "fiona 123 urn:foo:fiona",
                }
            ).enter("mike", "anything").has(),
            new IsEqual<>(false)
        );
    }
}
