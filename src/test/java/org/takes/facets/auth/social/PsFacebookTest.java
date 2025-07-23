/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth.social;

import com.jcabi.http.request.FakeRequest;
import com.restfb.DefaultWebRequestor;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;

/**
 * Test case for {@link PsFacebook}.
 * @since 0.15
 */
final class PsFacebookTest {

    @Test
    void canLogin() throws Exception {
        final String identifier = RandomStringUtils.randomAlphanumeric(10);
        final RandomStringGenerator generator =
            new RandomStringGenerator.Builder()
                .filteredBy(
                    Character::isLetterOrDigit, Character::isIdeographic
                ).build();
        final Pass pass = new PsFacebook(
            new FakeRequest(
                200,
                "HTTP OK",
                Collections.emptyList(),
                String.format(
                    "access_token=%s",
                    RandomStringUtils.randomAlphanumeric(10)
                ).getBytes(StandardCharsets.UTF_8)
            ),
            new DefaultWebRequestor() {
                // @checkstyle MethodArgumentCouldBeFinal
                @Override
                public Response executeGet(final String url) {
                    return new Response(
                        HttpURLConnection.HTTP_OK,
                        String.format(
                            "{\"id\":\"%s\",\"name\":\"%s\"}",
                            identifier,
                            generator.generate(10)
                        )
                    );
                }
            },
            generator.generate(10),
            generator.generate(10)
        );
        final Opt<Identity> identity = pass.enter(
            new RqFake(
                "GET",
                String.format(
                    "?code=%s",
                    RandomStringUtils.randomAlphanumeric(10)
                )
            )
        );
        MatcherAssert.assertThat(
            "Identity must be present after successful Facebook login",
            identity.has(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "Identity URN must match expected Facebook format",
            identity.get().urn(),
            CoreMatchers.equalTo(String.format("urn:facebook:%s", identifier))
        );
    }
}
