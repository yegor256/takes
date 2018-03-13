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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Body Matcher.
 *
 * <p>This "matcher" tests given item body</p>
 *
 * @author Tolegen Izbassar (t.izbassar@gmail.com)
 * @version $Id$
 * @param <T> Item type. Should be able to return own item
 * @since 2.0
 *
 * @todo #794:30min Current implementation of `AbstractHmBody` should be
 *  converted to `HmBytesBody` that will check equality of bytes. We can think
 *  of improving that class lately.
 */
abstract class AbstractHmBody<T> extends TypeSafeMatcher<T> {

    /**
     * Body.
     */
    private final InputStream body;

    /**
     * Ctor.
     * @param value Value to test against.
     */
    protected AbstractHmBody(final InputStream value) {
        super();
        this.body = value;
    }

    // @todo #795:30min Right now the describeTo method do not covered
    //  with tests. Cover this method with unit test to increase coverage
    //  of the class.
    @Override
    public final void describeTo(final Description description) {
        try {
            description.appendText("body: ")
                .appendText(Arrays.toString(AbstractHmBody.asBytes(this.body)));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected void describeMismatchSafely(final T item,
        final Description description) {
        try {
            description.appendText("body was: ")
                .appendText(
                    Arrays.toString(
                        AbstractHmBody.asBytes(this.itemBody(item))
                    )
                );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected final boolean matchesSafely(final T item) {
        boolean result = true;
        try (
            final InputStream val = new BufferedInputStream(this.itemBody(item))
        ) {
            int left = this.body.read();
            while (left != -1) {
                final int right = val.read();
                if (left != right) {
                    result = false;
                    break;
                }
                left = this.body.read();
            }
            final int right = val.read();
            if (result) {
                result = right == -1;
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        return result;
    }

    /**
     * Item's body.
     * @param item Item to retrieve body from
     * @return InputStream of body
     * @throws IOException If some problem inside
     */
    protected abstract InputStream itemBody(final T item) throws IOException;

    /**
     * InputStream as bytes.
     * @param input Input
     * @return Bytes
     * @throws IOException If some problem inside
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private static byte[] asBytes(final InputStream input) throws IOException {
        input.reset();
        try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            // @checkstyle MagicNumberCheck (1 line)
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
            return output.toByteArray();
        }
    }
}
