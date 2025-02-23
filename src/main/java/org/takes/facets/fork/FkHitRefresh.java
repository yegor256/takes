/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.EqualsAndHashCode;
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
 * @since 0.9
 * @see TkFork
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
     * @param tke Target
     */
    public FkHitRefresh(final File file, final String cmd, final Take tke) {
        this(file, Arrays.asList(cmd.split(" ")), tke);
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param tke Target
     */
    public FkHitRefresh(final File file, final List<String> cmd,
        final Take tke) {
        this(
            file,
            () -> {
                try {
                    new ProcessBuilder().command(cmd).start();
                } catch (final IOException ex) {
                    throw new IllegalStateException(
                        String.format("Failed to run command '%s'", cmd),
                        ex
                    );
                }
            },
            tke
        );
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param tke Target
     */
    public FkHitRefresh(final File file, final Runnable cmd, final Take tke) {
        this(
            cmd,
            tke,
            new HitRefreshHandle(file)
        );
    }

    /**
     * Ctor.
     * @param cmd Command to execute
     * @param tke Target
     * @param handle Hit refresh handle
     */
    private FkHitRefresh(final Runnable cmd, final Take tke,
        final HitRefreshHandle handle) {
        this.exec = cmd;
        this.take = tke;
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

    /**
     * A handle for serving hit-refresh feature.
     * @since 0.9
     */
    private static final class HitRefreshHandle {
        /**
         * Directory to watch.
         */
        private final File dir;

        /**
         * Internal state. Flag file touched on every exec run.
         * Instantiated at first touch.
         */
        private final List<File> flag;

        /**
         * A lock for concurrent access to flag file.
         */
        private final ReentrantReadWriteLock lock;

        /**
         * Ctor.
         * @param dir Directory to watch
         */
        HitRefreshHandle(final File dir) {
            this(
                dir,
                new ReentrantReadWriteLock()
            );
        }

        /**
         * Ctor.
         * @param dir Directory to watch
         * @param lock Lock for access to flag file
         */
        private HitRefreshHandle(final File dir,
            final ReentrantReadWriteLock lock) {
            this.dir = dir;
            this.lock = lock;
            this.flag = new CopyOnWriteArrayList<>();
        }

        /**
         * Create the file to touch, if it is not yet created.
         * @return The file to touch
         * @throws IOException If fails
         */
        public File touchedFile() throws IOException {
            if (this.flag.isEmpty()) {
                this.lock.writeLock().lock();
                final File file = File.createTempFile("take", ".txt");
                file.deleteOnExit();
                this.flag.add(file);
                this.lock.writeLock().unlock();
                this.touch();
            }
            return this.flag.get(0);
        }

        /**
         * Touch the temporary file.
         * @throws IOException If fails
         */
        public void touch() throws IOException {
            try (OutputStream out = Files.newOutputStream(
                this.touchedFile().toPath()
            )) {
                out.write('+');
            }
        }

        /**
         * Expired?
         * @return TRUE if expired
         */
        private boolean expired() {
            final boolean expired;
            if (this.flag.isEmpty()) {
                expired = true;
            } else {
                expired = this.directoryUpdated();
            }
            return expired;
        }

        /**
         * Directory contents updated?
         * @return TRUE if contents were updated
         */
        private boolean directoryUpdated() {
            final long recent;
            try {
                this.lock.readLock().lock();
                recent = this.flag.get(0).lastModified();
            } finally {
                this.lock.readLock().unlock();
            }
            final File[] files = this.dir.listFiles();
            boolean expired = false;
            if (new Opt.Single<>(files).has()) {
                for (final File file : files) {
                    if (file.lastModified() > recent) {
                        expired = true;
                        break;
                    }
                }
            }
            return expired;
        }
    }
}
