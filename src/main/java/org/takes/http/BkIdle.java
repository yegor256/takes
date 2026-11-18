/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import lombok.EqualsAndHashCode;

/**
 * Back-end decorator that bounds how long a connection may stay idle.
 *
 * <p>A keep-alive client may hold its connection open without sending
 * anything. Without a read timeout the back-end would wait for the next
 * request forever and keep a thread or the whole server busy. This
 * decorator sets the socket read timeout before handing the socket to
 * the original back, so that an idle connection ends once the timeout
 * expires.</p>
 *
 * <p>The class is immutable and thread-safe.</p>
 *
 * @since 1.20
 */
@EqualsAndHashCode(callSuper = true)
public final class BkIdle extends BkWrap {

    /**
     * Ctor.
     *
     * @param back Original back
     * @param msec Idle timeout in milliseconds
     */
    public BkIdle(final Back back, final long msec) {
        super(
            socket -> {
                socket.setSoTimeout(Math.toIntExact(msec));
                back.accept(socket);
            }
        );
    }
}
