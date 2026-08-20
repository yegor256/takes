/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.number.NumberOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;
import org.cactoos.text.UncheckedText;

/**
 * Main method remote control.
 *
 * <p>This class allows you to start an application with a {@code main()}
 * method in a separate thread, execute a script against it while it's running,
 * and then shut it down. This is particularly useful for integration testing
 * of applications that expose an HTTP interface through their main method.
 * The class automatically manages port allocation and cleanup.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.23
 */
@EqualsAndHashCode
public final class MainRemote {

    /**
     * Application with {@code main()} method.
     */
    private final Class<?> app;

    /**
     * Additional arguments to be passed to the main class.
     */
    private final String[] args;

    /**
     * Ctor.
     * @param type Class with main method
     */
    public MainRemote(final Class<?> type) {
        this(type, new String[0]);
    }

    /**
     * Ctor.
     * @param type Class with main method
     * @param passed Additional arguments to be passed to the main method
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    public MainRemote(final Class<?> type, final String... passed) {
        this.app = type;
        this.args = passed;
    }

    /**
     * Execute this script against a running front.
     * @param script Script to run
     * @throws Exception If fails
     */
    public void exec(final MainRemote.Script script) throws Exception {
        final File file = File.createTempFile("takes-", ".txt");
        if (!file.delete()) {
            throw new IOException(
                new UncheckedText(
                    new FormattedText(
                        "The temporary file '%s' could not be deleted before calling the exec method",
                        file.getAbsolutePath()
                    )
                ).asString()
            );
        }
        final String[] passed = new String[1 + this.args.length];
        passed[0] = new UncheckedText(
            new FormattedText(
                "--port=%s", file.getAbsoluteFile()
            )
        ).asString();
        for (int idx = 0; idx < this.args.length; ++idx) {
            passed[idx + 1] = this.args[idx];
        }
        final Thread thread = new Thread(
            new MainMethod(
                this.app.getDeclaredMethod("main", String[].class),
                passed
            )
        );
        thread.start();
        try {
            script.exec(
                URI.create(
                    new UncheckedText(
                        new FormattedText(
                            "http://localhost:%d",
                            MainRemote.port(file)
                        )
                    ).asString()
                )
            );
        } catch (final IOException ex) {
            if (!file.delete()) {
                ex.addSuppressed(
                    new IOException(
                        new UncheckedText(
                            new FormattedText(
                                "The temporary file '%s' could not be deleted while catching the error",
                                file.getAbsolutePath()
                            )
                        ).asString()
                    )
                );
            }
            throw ex;
        } finally {
            thread.interrupt();
        }
        if (!file.delete()) {
            throw new IOException(
                new UncheckedText(
                    new FormattedText(
                        "The temporary file '%s' could not be deleted after calling the exec method",
                        file.getAbsolutePath()
                    )
                ).asString()
            );
        }
    }

    private static int port(final File file) throws Exception {
        while (!file.exists()) {
            TimeUnit.MILLISECONDS.sleep(1L);
        }
        final int port;
        try (InputStream input = new InputOf(file).stream()) {
            final byte[] buf = new byte[10];
            while (true) {
                if (input.read(buf) > 0) {
                    break;
                }
            }
            port = new NumberOf(
                new Trimmed(new TextOf(new BytesOf(buf)))
            ).intValue();
        }
        return port;
    }

    /**
     * Script to execute.
     * @since 0.23
     */
    @FunctionalInterface
    public interface Script {

        /**
         * Execute it against this URI.
         * @param home URI of the running front
         * @throws IOException If fails
         */
        void exec(URI home) throws IOException;
    }
}
