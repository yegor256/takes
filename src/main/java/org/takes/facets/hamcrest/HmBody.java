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

package org.takes.facets.hamcrest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.takes.Body;

/**
 * Body Matcher.
 *
 * <p>This "matcher" tests given item body</p>
 *
 * @param <T> Item type. Should be able to return own item
 * @since 2.0
 */
public final class HmBody<T extends Body> extends TypeSafeMatcher<T> {

    /**
     * Body.
     */
    private final InputStream body;

    /**
     * Ctor.
     *
     * <p>Will create instance with defaultCharset.
     * @param value Value to test against
     */
    public HmBody(final String value) {
        this(value, Charset.defaultCharset());
    }

    /**
     * Ctor.
     * @param value Value to test against
     * @param charset Charset of given value
     */
    public HmBody(final String value, final Charset charset) {
        this(value.getBytes(charset));
    }

    /**
     * Ctor.
     * @param value Value to test against
     */
    public HmBody(final byte[] value) {
        this(new ByteArrayInputStream(value));
    }

    /**
     * Ctor.
     * @param value Value to test against.
     */
    public HmBody(final InputStream value) {
        super();
        this.body = value;
    }

    @Override
    public void describeTo(final Description description) {
        try {
            description.appendText("body: ")
                .appendText(Arrays.toString(HmBody.asBytes(this.body)));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void describeMismatchSafely(final T item,
        final Description description) {
        try {
            description.appendText("body was: ")
                .appendText(
                    Arrays.toString(
                        HmBody.asBytes(HmBody.itemBody(item))
                    )
                );
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public boolean matchesSafely(final T item) {
        boolean result = true;
        try (
            InputStream val = new BufferedInputStream(HmBody.itemBody(item))
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
    private static InputStream itemBody(final Body item) throws IOException {
        return item.body();
    }

    /**
     * InputStream as bytes.
     * @param input Input
     * @return Bytes
     * @throws IOException If some problem inside
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private static byte[] asBytes(final InputStream input) throws IOException {
        input.reset();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
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
