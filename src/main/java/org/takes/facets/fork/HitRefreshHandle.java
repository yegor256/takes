/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.cactoos.io.OutputTo;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.ScalarOf;
import org.takes.misc.Opt;

/**
 * A handle for serving hit-refresh feature.
 * @since 0.9
 */
final class HitRefreshHandle {

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
    HitRefreshHandle(final File dir, final ReentrantReadWriteLock lock) {
        this.dir = dir;
        this.lock = lock;
        this.flag = new CopyOnWriteArrayList<>();
    }

    /**
     * Create the file to touch, if it is not yet created.
     * @return The file to touch
     * @throws IOException If fails
     */
    File touchedFile() throws IOException {
        if (this.flag.isEmpty()) {
            this.lock.writeLock().lock();
            try {
                final File file = File.createTempFile("take", ".txt");
                file.deleteOnExit();
                this.flag.add(file);
            } finally {
                this.lock.writeLock().unlock();
            }
            this.touch();
        }
        return this.flag.get(0);
    }

    /**
     * Touch the temporary file.
     * @throws IOException If fails
     */
    void touch() throws IOException {
        try (
            OutputStream out = new IoChecked<>(
                new ScalarOf<>(
                    () -> new OutputTo(this.touchedFile()).stream()
                )
            ).value()
        ) {
            out.write('+');
        }
    }

    boolean expired() {
        final boolean expired;
        if (this.flag.isEmpty()) {
            expired = true;
        } else {
            expired = this.directoryUpdated();
        }
        return expired;
    }

    private boolean directoryUpdated() {
        final long recent;
        this.lock.readLock().lock();
        try {
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
