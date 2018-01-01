/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
package org.takes.facets.auth.social;

import com.jcabi.http.request.FakeRequest;
import com.restfb.DefaultWebRequestor;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Opt;
import org.takes.rq.RqFake;

/**
 * Test case for {@link PsFacebook}.
 * @author Aleksey Popov (alopen@yandex.ru)
 * @version $Id$
 * @since 0.15
 * @checkstyle MagicNumberCheck (500 lines)
 */
public class PsFacebookTest {

    /**
     * Tests if PsFacebook can call DefaultFacebookClient APIs.
     * @throws Exception if any error occurs
     */
    @Test
    public final void canLogin() throws Exception {
        final String identifier = RandomStringUtils.randomAlphanumeric(10);
        final Pass pass = new PsFacebook(
            new FakeRequest(
                200,
                "HTTP OK",
                Collections.<Map.Entry<String, String>>emptyList(),
                String.format(
                    "access_token=%s",
                    RandomStringUtils.randomAlphanumeric(10)
                ).getBytes("utf-8")
            ),
            new DefaultWebRequestor() {
                @Override
                public Response executeGet(final String url) {
                    return new Response(
                        HttpURLConnection.HTTP_OK,
                        String.format(
                            "{\"id\":\"%s\",\"name\":\"%s\"}",
                            identifier,
                            RandomStringUtils.random(10)
                        )
                    );
                }
            },
            RandomStringUtils.random(10),
            RandomStringUtils.random(10)
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
        MatcherAssert.assertThat(identity.has(), Matchers.is(true));
        MatcherAssert.assertThat(
            identity.get().urn(),
            CoreMatchers.equalTo(String.format("urn:facebook:%s", identifier))
        );
    }
}
