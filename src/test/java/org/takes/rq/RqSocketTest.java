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
package org.takes.rq;

import java.io.IOException;
import java.net.InetAddress;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.HttpException;

/**
 * Test case for {@link RqSocket}.
 * @author Igor Khvostenkov (ikhvostenkov@gmail.com)
 * @version $Id$
 * @since 1.0
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public final class RqSocketTest {

    /**
     * RqSocket can return local address.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnLocalAddress() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(
                    new RqFake(), "X-Takes-LocalAddress: 192.168.134.1"
                )
            ).getLocalAddress(),
            Matchers.is(InetAddress.getByName("192.168.134.1"))
        );
    }

    /**
     * RqSocket can return local port.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnLocalPort() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-LocalPort: 55555")
            ).getLocalPort(),
            Matchers.is(55555)
        );
    }

    /**
     * RqSocket can return remote address.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnRemoteAddress() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(
                    new RqFake(), "X-Takes-RemoteAddress: 10.233.189.20"
                )
            ).getRemoteAddress(),
            Matchers.is(InetAddress.getByName("10.233.189.20"))
        );
    }

    /**
     * RqSocket can return remote port.
     * @throws IOException If some problem inside
     */
    @Test
    public void returnRemotePort() throws IOException {
        MatcherAssert.assertThat(
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-RemotePort: 80")
            ).getRemotePort(),
            Matchers.is(80)
        );
    }

    /**
     * RqSocket can return not found remote address.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void returnNotFoundRemoteAddress() throws IOException {
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
                    "header \"X-Takes-RemoteAddress\" is mandatory"
                )
            );
            throw ex;
        }
    }

    /**
     * RqSocket can return not found local address.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void returnNotFoundLocalAddress() throws IOException {
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
                    "header \"X-Takes-LocalAddress\" is mandatory"
                )
            );
            throw ex;
        }
    }

    /**
     * RqSocket can return not found remote port.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void returnNotFoundRemotePort() throws IOException {
        try {
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-NotFoundPort: 22")
            ).getRemotePort();
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString(
                    "header \"X-Takes-RemotePort\" is mandatory"
                )
            );
            throw ex;
        }
    }

    /**
     * RqSocket can return not found local port.
     * @throws IOException If some problem inside
     */
    @Test(expected = HttpException.class)
    public void returnNotFoundLocalPort() throws IOException {
        try {
            new RqSocket(
                new RqWithHeader(new RqFake(), "X-Takes-NotFoundPort: 80")
            ).getLocalPort();
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                ex.getMessage(),
                Matchers.containsString(
                    "header \"X-Takes-LocalPort\" is mandatory"
                )
            );
            throw ex;
        }
    }

    /**
     * Checks RqSocket equals method.
     * @throws Exception If some problem inside
     */
    @Test
    public void equalsAndHashCodeEqualTest() throws Exception {
        EqualsVerifier.forClass(RqSocket.class)
            .suppress(Warning.TRANSIENT_FIELDS)
            .withRedefinedSuperclass()
            .verify();
    }
}
