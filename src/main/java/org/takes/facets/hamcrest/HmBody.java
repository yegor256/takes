/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
