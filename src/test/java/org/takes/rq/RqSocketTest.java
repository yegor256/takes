/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
            "Local address must be parsed from X-Takes-LocalAddress header",
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
            "Local port must be parsed from X-Takes-LocalPort header",
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-LocalPort: 55555")
            ).getLocalPort(),
            Matchers.is(55_555)
        );
    }

    @Test
    void returnRemoteAddress() throws IOException {
        MatcherAssert.assertThat(
            "Remote address must be parsed from X-Takes-RemoteAddress header",
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
            "Remote port must be parsed from X-Takes-RemotePort header",
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
                        "Exception message must indicate missing remote address header",
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
                        "Exception message must indicate missing local address header",
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
                        "Exception message must indicate missing remote port header",
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
                        "Exception message must indicate missing local port header",
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
