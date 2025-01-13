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
package org.takes.rq;

import java.io.IOException;
import java.net.InetAddress;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.takes.HttpException;
import org.takes.Request;

/**
 * Test case for {@link RqSocket}.
 * @since 1.0
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
final class RqSocketTest {

    @Test
    void returnLocalAddress() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(
                    new RqFake(), "X-Takes-LocalAddress: 192.168.134.1"
                )
            ).getLocalAddress(),
            Matchers.is(InetAddress.getByName("192.168.134.1"))
        );
    }

    @Test
    void returnLocalPort() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-LocalPort: 55555")
            ).getLocalPort(),
            Matchers.is(55_555)
        );
    }

    @Test
    void returnRemoteAddress() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(
                    new RqFake(), "X-Takes-RemoteAddress: 10.233.189.20"
                )
            ).getRemoteAddress(),
            Matchers.is(InetAddress.getByName("10.233.189.20"))
        );
    }

    @Test
    void returnRemotePort() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-RemotePort: 80")
            ).getRemotePort(),
            Matchers.is(80)
        );
    }

    @Test
    void returnNotFoundRemoteAddress() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                try {
                    new RqSocket(
                        new RqWithHeader(
                            new RqFake(), "X-Takes-NotFoundInetAddress: x.x.x.x"
                        )
                    ).getRemoteAddress();
                } catch (final HttpException ex) {
                    MatcherAssert.assertThat(
                        ex.getMessage(),
                        Matchers.containsString(
                            "Header \"X-Takes-RemoteAddress\" is mandatory"
                        )
                    );
                    throw ex;
                }
            }
        );
    }

    @Test
    void returnNotFoundLocalAddress() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                try {
                    new RqSocket(
                        new RqWithHeader(
                            new RqFake(), "X-Takes-NotFoundInetAddress: 10.233.189.20"
                        )
                    ).getLocalAddress();
                } catch (final HttpException ex) {
                    MatcherAssert.assertThat(
                        ex.getMessage(),
                        Matchers.containsString(
                            "Header \"X-Takes-LocalAddress\" is mandatory"
                        )
                    );
                    throw ex;
                }
            }
        );
    }

    @Test
    void returnNotFoundRemotePort() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                try {
                    new RqSocket(
                        new RqWithHeader(new RqFake(), "X-Takes-NotFoundPort: 22")
                    ).getRemotePort();
                } catch (final HttpException ex) {
                    MatcherAssert.assertThat(
                        ex.getMessage(),
                        Matchers.containsString(
                            "Header \"X-Takes-RemotePort\" is mandatory"
                        )
                    );
                    throw ex;
                }
            }
        );
    }

    @Test
    void returnNotFoundLocalPort() {
        Assertions.assertThrows(
            HttpException.class,
            () -> {
                try {
                    new RqSocket(
                        new RqWithHeader(new RqFake(), "X-Takes-NotFoundPort: 80")
                    ).getLocalPort();
                } catch (final HttpException ex) {
                    MatcherAssert.assertThat(
                        ex.getMessage(),
                        Matchers.containsString(
                            "Header \"X-Takes-LocalPort\" is mandatory"
                        )
                    );
                    throw ex;
                }
            }
        );
    }

    @Test
    void mustEqualToSameTypeRequest() {
        final Request request = new RqWithHeader(
            new RqFake(), "X-Takes-LocalPort: 55555"
        );
        new Assertion<>(
            "RqSocket must equal to other RqSocket",
            new RqSocket(
                request
            ),
            new IsEqual<>(
                new RqSocket(
                    request
                )
            )
        ).affirm();
    }

    @Test
    void hashCodeMustEqualToSameTypeRequest() {
        final Request request = new RqWithHeader(
            new RqFake(), "X-Takes-LocalPort: 55555"
        );
        new Assertion<>(
            "RqSocket must equal to other RqSocket",
            new RqSocket(
                request
            ).hashCode(),
            new IsEqual<>(
                new RqSocket(
                    request
                ).hashCode()
            )
        ).affirm();
    }

}
