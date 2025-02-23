/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;

/**
 * Take that always fails.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkFailure extends TkWrap {

    /**
     * Ctor.
     */
    public TkFailure() {
        this("Intentional failure");
    }

    /**
     * Ctor.
     * @param err Error to throw
     */
    public TkFailure(final String err) {
        this(new IllegalStateException(err));
    }

    /**
     * Ctor.
     * @param err Error to throw
     */
    public TkFailure(final RuntimeException err) {
        super(
            request -> {
                throw err;
            }
        );
    }

    /**
     * Ctor.
     * @param err Error to throw
     * @since 1.4
     */
    public TkFailure(final Scalar<IOException> err) {
        super(
            request -> {
                throw new IoChecked<>(err).value();
            }
        );
    }

    /**
     * Ctor.
     * @param err Error to throw
     * @since 0.27
     */
    public TkFailure(final IOException err) {
        super(
            request -> {
                throw err;
            }
        );
    }

}
