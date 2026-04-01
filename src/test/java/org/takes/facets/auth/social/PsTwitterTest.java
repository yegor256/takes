/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.facets.auth.social;

import com.jcabi.http.request.FakeRequest;
import jakarta.json.Json;
import java.nio.charset.StandardCharsets;
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class PsTwitterTest {

    @Test
    void identityUrnMatchesTwitterFormat() throws Exception {
        final int tid = 123;
        MatcherAssert.assertThat(
            "Identity URN must match expected Twitter format",
            PsTwitterTest.login(tid, "testname", "testpic").urn(),
            CoreMatchers.equalTo(String.format("urn:twitter:%d", tid))
        );
    }

    @Test
    void identityNamePropertyMatches() throws Exception {
        final String randname = "testuser";
        MatcherAssert.assertThat(
            "Identity name property must match expected value",
            PsTwitterTest.login(456, randname, "pic").properties().get("name"),
            CoreMatchers.equalTo(randname)
        );
    }

    @Test
    void identityPicturePropertyMatches() throws Exception {
        final String picture = "mypic.jpg";
        MatcherAssert.assertThat(
            "Identity picture property must match expected value",
            PsTwitterTest.login(789, "user", picture).properties().get("picture"),
            CoreMatchers.equalTo(picture)
        );
    }

    private static Identity login(
        final int tid,
        final String name,
        final String picture
    ) throws Exception {
        final String httpok = "HTTP OK";
        final Pass pass = new PsTwitter(
            new FakeRequest(
                200,
                httpok,
                Collections.emptyList(),
                String.format(
                    "{\"token_type\":\"bearer\",\"access_token\":\"%s\"}",
                    RandomStringUtils.randomAlphanumeric(10)
                ).getBytes(StandardCharsets.UTF_8)
            ),
            new FakeRequest(
                200,
                httpok,
                Collections.emptyList(),
                Json.createObjectBuilder()
                    .add("id", tid)
                    .add("name", name)
                    .add("profile_image_url", picture)
                    .build()
                    .toString()
                    .getBytes(StandardCharsets.UTF_8)
            ),
            RandomStringUtils.randomAlphanumeric(10),
            RandomStringUtils.randomAlphanumeric(10)
        );
        return pass.enter(new RqFake("GET", "")).get();
    }
}
