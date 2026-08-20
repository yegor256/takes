/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rq.form;

/**
 * CharSequence whose value is computed lazily from URL-encoded parameters.
 * @since 2.0
 */
@SuppressWarnings("PMD.ArrayIsStoredDirectly")
final class FakeFormBody implements CharSequence {

    /**
     * Source parameters.
     */
    private final String[] params;

    /**
     * Ctor.
     * @param all Parameters
     */
    FakeFormBody(final String... all) {
        this.params = all;
    }

    @SuppressWarnings("PMD.UseStringBufferLength")
    @Override
    public int length() {
        return this.toString().length();
    }

    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        return RqFormFake.construct(RqFormFake.validated(this.params));
    }
}
