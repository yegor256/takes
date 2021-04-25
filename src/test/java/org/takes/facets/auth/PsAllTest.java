/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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

import com.jcabi.aspects.Tv;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsEmpty;

/**
 * Test of {@link PsAll}.
 * @since 0.22
 */
final class PsAllTest {

    /**
     * Fails if PsAll with 0 Passes is created.
     */
    @Test
    void thereShouldBeAtLeastOnePass() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
                MatcherAssert.assertThat(
                    new PsAll(
                        new ArrayList<Pass>(0),
                        0
                    ).enter(new RqFake()).has(),
                    new IsEqual<>(false)
                );
            }
        );
    }

    /**
     * Fails if index is less then 0.
     */
    @Test
    void indexMustBeNonNegative() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
                MatcherAssert.assertThat(
                    new PsAll(
                        Collections.singletonList(new PsFake(true)),
                        -1
                    ).enter(new RqFake()).has(),
                    new IsEqual<>(false)
                );
            }
        );
    }

    /**
     * Fails if index is greater or equal to the number of Passes to enter.
     */
    @Test
    void indexMustBeSmallEnough() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> {
                MatcherAssert.assertThat(
                    new PsAll(
                        Arrays.asList(
                            new PsFake(true),
                            new PsFake(false)
                        ),
                        2
                    ).enter(new RqFake()).has(),
                    new IsEqual<>(false)
                );
            }
        );
    }

    /**
     * PsAll with a single Pass that can be entered should succeed.
     * @throws Exception If fails
     */
    @Test
    void testOneSuccessfull() throws Exception {
        MatcherAssert.assertThat(
            new PsAll(
                Collections.singletonList(new PsFake(true)),
                0
            ).enter(new RqFake()).has(),
            new IsEqual<>(true)
        );
    }

    /**
     * Fail with only one failure test.
     * @throws Exception if exception
     */
    @Test
    void testOneFail() throws Exception {
        MatcherAssert.assertThat(
            new PsAll(
                Collections.singletonList(new PsFake(false)),
                0
            ).enter(new RqFake()).has(),
            new IsEqual<>(false)
        );
    }

    /**
     * PsAll with multiple passes that all can be entered returns the
     * identity specified by an index.
     * @throws Exception If fails
     */
    @Test
    void testSuccessfullIdx() throws Exception {
        final Pass resulting = new PsFixed(
            new Identity.Simple("urn:foo:test")
        );
        final RqFake request = new RqFake();
        MatcherAssert.assertThat(
            new PsAll(
                Arrays.asList(
                    new PsFake(true),
                    new PsFake(true),
                    new PsFake(true),
                    resulting
                ),
                Tv.THREE
            ).enter(request).get().urn(),
            new IsEqual<>(resulting.enter(request).get().urn())
        );
    }

    /**
     * Fail if one of Pass fails.
     * @throws Exception if exception
     */
    @Test
    void testFail() throws Exception {
        MatcherAssert.assertThat(
            new PsAll(
                Arrays.asList(
                    new PsFake(true),
                    new PsFake(true),
                    new PsFake(false),
                    new PsFake(true)
                ),
                1
            ).enter(new RqFake()).has(),
            new IsEqual<>(false)
        );
    }

    /**
     * Exits.
     * @throws Exception If fails
     */
    @Test
    void exits() throws Exception {
        final Response response = new RsEmpty();
        final PsFake exiting = new PsFake(true);
        MatcherAssert.assertThat(
            new PsAll(
                Arrays.asList(
                    new PsFake(true),
                    exiting
                ),
                1
            ).exit(response, exiting.enter(new RqFake()).get()),
            new IsEqual<>(response)
        );
    }
}
