/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Fork by hit-refresh header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @see TkFork
 * @since 0.9
 */
@EqualsAndHashCode
public final class FkHitRefresh implements Fork {

    /**
     * Command to execute.
     */
    private final Runnable exec;

    /**
     * Target.
     */
    private final Take take;

    /**
     * A handle for tracking changes.
     */
    private final HitRefreshHandle handle;

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param that Target
     */
    public FkHitRefresh(final File file, final List<String> cmd,
        final Take that) {
        this(
            file,
            () -> {
                try {
                    new ProcessBuilder().command(cmd).start();
                } catch (final IOException ex) {
                    throw new IllegalStateException(
                        new UncheckedText(
                            new FormattedText(
                                "Failed to run command '%s'", cmd
                            )
                        ).asString(),
                        ex
                    );
                }
            },
            that
        );
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param that Target
     */
    public FkHitRefresh(final File file, final Runnable cmd, final Take that) {
        this(
            cmd,
            that,
            new HitRefreshHandle(file)
        );
    }

    /**
     * Ctor.
     * @param cmd Command to execute
     * @param that Target
     * @param handle Hit refresh handle
     */
    private FkHitRefresh(final Runnable cmd, final Take that,
        final HitRefreshHandle handle) {
        this.exec = cmd;
        this.take = that;
        this.handle = handle;
    }

    @Override
    public Opt<Response> route(final Request req) throws Exception {
        final Iterator<String> header =
            new RqHeaders.Base(req).header("X-Takes-HitRefresh").iterator();
        final Opt<Response> resp;
        if (header.hasNext()) {
            if (this.handle.expired()) {
                this.exec.run();
                this.handle.touch();
            }
            resp = new Opt.Single<>(this.take.act(req));
        } else {
            resp = new Opt.Empty<>();
        }
        return resp;
    }
}
