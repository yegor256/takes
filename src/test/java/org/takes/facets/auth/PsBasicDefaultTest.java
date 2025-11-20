/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.net.URLEncoder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link org.takes.facets.auth.PsBasic.Default}.
 * @since 0.22
 */
final class PsBasicDefaultTest {

    @Test
    void acceptsCorrectLoginPasswordPair() {
        MatcherAssert.assertThat(
            "PsBasic.Default must authenticate user with correct login/password and return URN",
            new PsBasic.Default(
                new String[]{
                    "bob qwe%20r%20ty%3A%2B urn:foo:robert",
                    "alice пароль urn:foo:alice",
                }
            )
                .enter(
                    "bob",
                    "qwe r ty:+"
                )
                .get()
                .urn(),
            new IsEqual<>("urn:foo:robert")
        );
    }

    @Test
    void supportsBothKindsOfSpace() {
        MatcherAssert.assertThat(
            "PsBasic.Default must support URL-encoded spaces in passwords",
            new PsBasic.Default(
                new String[]{
                    "yvonne hey%20you urn:foo:z",
                }
            )
                .enter(
                    "yvonne",
                    "hey you"
                )
                .has(),
            new IsEqual<>(true)
        );
        MatcherAssert.assertThat(
            "PsBasic.Default must support plus-encoded spaces in passwords",
            new PsBasic.Default(
                new String[]{
                    "zak hey+me urn:foo:z",
                }
            )
                .enter(
                    "zak",
                    "hey me"
                )
                .has(),
            new IsEqual<>(true)
        );
    }

    @Test
    void supportsUsersWithSpacesInTheirNames() {
        MatcherAssert.assertThat(
            "PsBasic.Default must support users with spaces in their names",
            new PsBasic.Default(
                new String[]{
                    "abraham+lincoln qwer urn:foo:z",
                }
            )
                .enter(
                    "abraham lincoln",
                    "qwer"
                )
                .has(),
            new IsEqual<>(true)
        );
    }

    @Test
    void supportsUrlencodedUrns() throws Exception {
        final String urn = "urn:a100%25:one-two+";
        MatcherAssert.assertThat(
            "PsBasic.Default must support URL-encoded URNs and decode them correctly",
            new PsBasic.Default(
                new String[]{
                    String.format(
                        "login password %s",
                        URLEncoder.encode(urn, "UTF-8")
                    ),
                }
            )
                .enter(
                    "login",
                    "password"
                )
                .get()
                .urn(),
            new IsEqual<>(urn)
        );
    }

    @Test
    void rejectsIncorrectPassword() {
        MatcherAssert.assertThat(
            "PsBasic.Default must reject authentication with incorrect password",
            new PsBasic.Default(
                new String[]{
                    "charlie qwerty urn:foo:charlie",
                    "doreen 123 urn:foo:doreen",
                }
            )
                .enter("charlie", "wrongpassword")
                .has(),
            new IsEqual<>(false)
        );
    }

    @Test
    void rejectsIncorrectLogin() {
        MatcherAssert.assertThat(
            "PsBasic.Default must reject authentication with incorrect login",
            new PsBasic.Default(
                new String[]{
                    "eddie qwerty urn:foo:eddie",
                    "fiona 123 urn:foo:fiona",
                }
            )
                .enter("mike", "anything")
                .has(),
            new IsEqual<>(false)
        );
    }
}
