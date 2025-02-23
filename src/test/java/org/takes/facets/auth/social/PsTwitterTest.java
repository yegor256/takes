/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.facets.auth.social;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.util.Collections;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.rq.RqFake;

/**
 * Test case for {@link PsTwitter}.
 * @since 1.0
 */
final class PsTwitterTest {

    @Test
    void logsIn() throws Exception {
        final int tid = RandomUtils.nextInt(1000);
        final String randname = RandomStringUtils.randomAlphanumeric(10);
        final String picture = RandomStringUtils.randomAlphanumeric(10);
        final String httpok = "HTTP OK";
        final String name = "name";
        final Pass pass = new PsTwitter(
            new FakeRequest(
                200,
                httpok,
                Collections.emptyList(),
                String.format(
                    "{\"token_type\":\"bearer\",\"access_token\":\"%s\"}",
                    RandomStringUtils.randomAlphanumeric(10)
                ).getBytes()
            ),
            new FakeRequest(
                200,
                httpok,
                Collections.emptyList(),
                Json.createObjectBuilder()
                    .add("id", tid)
                    .add(name, randname)
                    .add("profile_image_url", picture)
                    .build()
                    .toString()
                    .getBytes()
            ),
            RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10)
        );
        final Identity identity = pass.enter(
            new RqFake("GET", "")
        ).get();
        MatcherAssert.assertThat(
            identity.urn(),
            CoreMatchers.equalTo(String.format("urn:twitter:%d", tid))
        );
        MatcherAssert.assertThat(
            identity.properties().get(name),
            CoreMatchers.equalTo(randname)
        );
        MatcherAssert.assertThat(
            identity.properties().get("picture"),
            CoreMatchers.equalTo(picture)
        );
    }
}
