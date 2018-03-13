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

package org.takes.facets.hamcrest;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.takes.Response;
import org.takes.rs.RsSimple;

/**
 * Test case for {@link HmRsBody}.
 *
 * @author Tolegen Izbassar (t.izbassar@gmail.com)
 * @version $Id$
 * @since 2.0
 */
public final class HmRsBodyTest {

    /**
     * HmRsBody can test if values of bodies are same.
     */
    @Test
    public void bodyValuesAreSame() {
        final String body = "Same";
        MatcherAssert.assertThat(
            new RsSimple(
                Collections.<String>emptyList(),
                body
            ),
            new HmRsBody(body)
        );
    }

    /**
     * HmRsBody can test if values of bodies are different.
     */
    @Test
    public void bodyValuesAreDifferent() {
        MatcherAssert.assertThat(
            new RsSimple(
                Collections.<String>emptyList(),
                "this"
            ),
            Matchers.not(new HmRsBody("that"))
        );
    }

    /**
     * HmRsBody can describe mismatch in readable way.
     */
    @Test
    public void describesMismatchInReadableWay() {
        final Response response = new RsSimple(
            Collections.<String>emptyList(),
            "other"
        );
        final HmRsBody matcher = new HmRsBody("some");
        matcher.matchesSafely(response);
        final StringDescription description = new StringDescription();
        matcher.describeMismatchSafely(response, description);
        MatcherAssert.assertThat(
            description.toString(),
            Matchers.equalTo(
                "body was: [111, 116, 104, 101, 114]"
            )
        );
    }
}
