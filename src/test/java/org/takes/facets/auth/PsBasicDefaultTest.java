/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
