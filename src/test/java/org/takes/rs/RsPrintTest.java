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
package org.takes.rs;

import java.io.IOException;
import java.io.OutputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link RsPrint}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.8
 */
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
     * RsPrint can flush head contents even when exception happens.
     * @throws IOException If some problem inside
     * @todo #778:30min Fix flushHeadEvenWhenExceptionHappens test
     *  and remove Ignore annotation. Implementation could not rely
     *  on simple try-finally block as OutputStreamWriter do not call
     *  to the flush() of OutputStream. It has additional checks.
     */
    @Test
    @Ignore
    public void flushHeadEvenWhenExceptionHappens() throws IOException {
        final IOException exception = new IOException("Error");
        final FailOutputStream output = new FailOutputStream(exception);
        try {
            new RsPrint(new RsText("World!"))
                .printHead(output);
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
     * FailOutputStream that throws IOException on method write.
     *
     * @author Izbassar Tolegen (t.izbassar@gmail.com)
     * @version $Id$
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
}
