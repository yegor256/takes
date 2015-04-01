/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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
import com.jcabi.immutable.Array;
import com.restfb.WebRequestor;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import javax.json.Json;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link PsFacebook}.
 * @author Dmitry Molotchko (dima.molotchko@gmail.com)
 * @version $Id$
 * @since 0.10.2
 */
public final class PsFacebookTest {

    /**
     * PsFacebook can return identity iterator.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnIdentityIterator() throws IOException {
        MatcherAssert.assertThat(
            new PsFacebook(
                new FakeRequest(
                    HttpURLConnection.HTTP_OK,
                    "",
                    new Array<Map.Entry<String, String>>(),
                    "access_token=fake".getBytes("utf-8")
                ),
                new WebRequestor.Response(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add("id", "0123456789")
                        .add("first_name", "Firstname")
                        .add("last_name", "Lastname")
                        .add("gender", "male")
                        .add("name", "Firstname Lastname")
                        .build().toString()
                )
            ).enter(
                new RqFake("GET", "http://fake.com?code=fakecode")
            ).next().urn().toString(),
            Matchers.equalTo("urn:facebook:0123456789")
        );
    }

}
