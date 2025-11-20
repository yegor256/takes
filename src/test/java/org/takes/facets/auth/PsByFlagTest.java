/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.mockito.Mockito;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithStatus;
import org.takes.rs.RsWithType;

/**
 * Test case for {@link PsByFlag}.
 * @since 0.10
 */
final class PsByFlagTest {
    @Test
    void skipsIfNothingFound() throws Exception {
        MatcherAssert.assertThat(
            "Flag not found must return false",
            new PsByFlag(
                new PsByFlag.Pair(
                    "test", new PsFake(true)
                )
            ).enter(
                new RqFake("GET", "/?PsByFlag=x")
            ).has(),
            Matchers.is(false)
        );
    }

    @Test
    void flagIsFoundUserAuthenticated() throws Exception {
        MatcherAssert.assertThat(
            "Matching flag must return expected identity URN",
            new PsByFlag(
                new PsByFlag.Pair(
                    "some-key", new PsFake(true)
                )
            ).enter(new RqFake("POST", "/?PsByFlag=some-key")).get()
                .urn(),
            Matchers.is("urn:test:1")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    void exitTest() {
        final Response response = new RsWithStatus(
            new RsWithType(
                new RsWithBody("<html>This is test response</html>"),
                "text/html"
            ),
            HttpURLConnection.HTTP_OK
        );
        MatcherAssert.assertThat(
            "Exit must return same response",
            new PsByFlag(
                new MapOf<>(
                    new MapEntry<>(
                        Pattern.compile("key"), new PsFake(true)
                    )
                )
            ).exit(response, Mockito.mock(Identity.class)),
            Matchers.is(response)
        );
    }

    @Test
    void mustEvaluateTrueEqualityTest() {
        final Map<Pattern, Pass> passes = new HashMap<>(1);
        passes.put(Pattern.compile("key"), new PsFake(true));
        new Assertion<>(
            "Must evaluate true equality",
            new PsByFlag(passes),
            new IsEqual<>(new PsByFlag(passes))
        ).affirm();
    }
}
