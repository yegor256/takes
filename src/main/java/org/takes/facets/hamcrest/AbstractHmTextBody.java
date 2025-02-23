/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.hamcrest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Text body matcher.
 *
 * <p>This "matcher" tests given item body, assuming that it has text content.
 * <p>The class is immutable and thread-safe.
 *
 * @param <T> Item type. Should be able to return own body
 * @since 2.0
 */
public abstract class AbstractHmTextBody<T> extends TypeSafeMatcher<T> {

    /**
     * Body matcher.
     */
    private final Matcher<String> body;

    /**
     * Charset of the text.
     */
    private final Charset charset;

    /**
     * Ctor.
     * @param body Body matcher.
     * @param charset Charset of the text.
     */
    public AbstractHmTextBody(final Matcher<String> body,
        final Charset charset) {
        super();
        this.body = body;
        this.charset = charset;
    }

    @Override
    public final void describeTo(final Description description) {
        description.appendText("body: ").appendDescriptionOf(this.body);
    }

    @Override
    protected final boolean matchesSafely(final T item) {
        try {
            return this.body.matches(this.text(item));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    protected final void describeMismatchSafely(final T item, final
        Description description) {
        try {
            description.appendText("body was: ").appendText(this.text(item));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Item's body.
     * @param item Item to retrieve body from
     * @return InputStream of body
     * @throws IOException If some problem inside
     */
    protected abstract InputStream itemBody(T item) throws IOException;

    /**
     * Text from item.
     * @param item Item
     * @return Text contents of item
     * @throws IOException If some problem inside
     */
    @SuppressWarnings("PMD.AssignmentInOperand")
    private String text(final T item) throws IOException {
        final String text;
        try (
            InputStream input = this.itemBody(item);
            ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();
            text = new String(output.toByteArray(), this.charset);
        }
        return text;
    }
}
