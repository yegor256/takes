/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.HttpException;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

/**
 * Take that makes all not-found exceptions location aware.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkVerbose extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     */
    public TkVerbose(final Take take) {
        super(
            request -> {
                try {
                    return take.act(request);
                } catch (final HttpException ex) {
                    throw new HttpException(
                        ex.code(),
                        String.format(
                            "%s %s",
                            new RqMethod.Base(request).method(),
                            new RqHref.Base(request).href()
                        ),
                        ex
                    );
                }
            }
        );
    }

}
