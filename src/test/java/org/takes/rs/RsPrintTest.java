/*
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
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.cactoos.io.InputStreamOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Test case for {@link RsPrint}.
 * @since 0.8
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class RsPrintTest {

    /**
     * RsPrint can fail on invalid chars.
     * @throws IOException If some problem inside
     */
    @Test(expected = IllegalArgumentException.class)
    public void failsOnInvalidHeader() throws IOException {
        new RsPrint(new RsWithHeader("name", "\n\n\n")).print();
    }

    /**
     * RsPrint can flush body contents even when exception happens.
     * @throws IOException If some problem inside
     */
    @Test
    public void flushBodyEvenWhenExceptionHappens() throws IOException {
        final IOException exception = new IOException("Failure");
        final FailOutputStream output = new FailOutputStream(exception);
        try {
            new RsPrint(new RsText("Hello"))
                .printBody(output);
        } catch (final IOException ex) {
            if (!ex.equals(exception)) {
                throw ex;
            }
        }
        MatcherAssert.assertThat(
            output.haveFlushed(),
            Matchers.is(true)
        );
    }

    /**
     * RsPrint can close body contents even when exception happens.
     * @throws IOException If some problem inside
     */
    @Test
    public void closeBodyEvenWhenExceptionHappens() throws IOException {
        final IOException exception = new IOException("Smth went wrong");
        final FailOutputStream output = new FailOutputStream(exception);
        final FakeInput input = new FakeInput(new InputStreamOf("abc"));
        try {
            new RsPrint(new RsText(input))
                .printBody(output);
        } catch (final IOException ex) {
            if (!ex.equals(exception)) {
                throw ex;
            }
        }
        MatcherAssert.assertThat(
            "Input body was not closed",
            input.isClosed(),
            new IsEqual<>(true)
        );
    }

    /**
     * RsPrint can flush head contents even when exception happens.
     * @throws IOException If some problem inside
     */
    @Test
    public void flushHeadEvenWhenExceptionHappens() throws IOException {
        final IOException exception = new IOException("Error");
        final FailWriter writer = new FailWriter(exception);
        try {
            new RsPrint(new RsText("World!"))
                .printHead(writer);
        } catch (final IOException ex) {
            if (!ex.equals(exception)) {
                throw ex;
            }
        }
        MatcherAssert.assertThat(
            writer.haveFlushed(),
            Matchers.is(true)
        );
    }

    /**
     * Writer that throws IOException on method write.
     *
     * @since 2.0
     */
    private static final class FailWriter extends Writer {

        /**
         * Failing output stream.
         */
        private final FailOutputStream output;

        /**
         * Ctor.
         * @param exception Exception to throw
         */
        FailWriter(final IOException exception) {
            super();
            this.output = new FailOutputStream(exception);
        }

        @Override
        public void write(final char[] cbuf, final int off, final int len)
            throws IOException {
            this.output.write(
                new String(cbuf).getBytes(StandardCharsets.UTF_8),
                off,
                len
            );
        }

        @Override
        public void flush() throws IOException {
            this.output.flush();
        }

        @Override
        public void close() throws IOException {
            this.output.close();
        }

        /**
         * Have contents been flushed?
         * @return True, if contents were flushed, false - otherwise
         */
        public boolean haveFlushed() {
            return this.output.haveFlushed();
        }
    }

    /**
     * FailOutputStream that throws IOException on method write.
     *
     * @since 2.0
     */
    private static final class FailOutputStream extends OutputStream {

        /**
         * Have contents been flushed?
         */
        private boolean flushed;

        /**
         * Exception, that needs to be thrown.
         */
        private final IOException exception;

        /**
         * Ctor.
         * @param exception Exception to throw
         */
        FailOutputStream(final IOException exception) {
            super();
            this.exception = exception;
        }

        @Override
        public void write(final int value) throws IOException {
            throw this.exception;
        }

        @Override
        public void flush() throws IOException {
            this.flushed = true;
        }

        /**
         * Have contents been flushed?
         * @return True, if contents were flushed, false - otherwise
         */
        public boolean haveFlushed() {
            return this.flushed;
        }
    }

    /**
     * Fake wrapper for InputStream to make sure body is closed.
     *
     * @author Alena Gerasimova (olena.gerasimova@gmail.com)
     * @version $Id$
     * @since 2.0
     */
    private static final class FakeInput extends InputStream {

        /**
         * Have input been closed?
         */
        private boolean closed;

        /**
         * Origin.
         */
        private final InputStream origin;

        /**
         * Ctor.
         * @param origin Origin input
         */
        FakeInput(final InputStream origin) {
            super();
            this.origin = origin;
        }

        @Override
        public int read() throws IOException {
            return this.origin.read();
        }

        @Override
        public void close() throws IOException {
            this.origin.close();
            this.closed = true;
        }

        /**
         * Have input been closed?
         * @return True, if input wes closed, false - otherwise
         */
        public boolean isClosed() {
            return this.closed;
        }
    }
}
