/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import org.cactoos.bytes.BytesOf;
import org.cactoos.number.NumberOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;

/**
 * Front remote control.
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
    public MainRemote(final Class<?> type, final String... passed) {
        this.app = type;
        this.args = Arrays.copyOf(passed, passed.length);
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
                String.format(
                    "The temporary file '%s' could not be deleted before calling the exec method",
                    file.getAbsolutePath()
                )
            );
        }
        final Method method = this.app.getDeclaredMethod(
            "main", String[].class
        );
        final String[] passed = new String[1 + this.args.length];
        passed[0] = String.format("--port=%s", file.getAbsoluteFile());
        for (int idx = 0; idx < this.args.length; ++idx) {
            passed[idx + 1] = this.args[idx];
        }
        final Thread thread = new Thread(new MainMethod(method, passed));
        thread.start();
        try {
            script.exec(
                URI.create(
                    String.format(
                        "http://localhost:%d",
                        MainRemote.port(file)
                    )
                )
            );
        } catch (final IOException ex) {
            if (!file.delete()) {
                ex.addSuppressed(
                    new IOException(
                        String.format(
                            "The temporary file '%s' could not be deleted while catching the error",
                            file.getAbsolutePath()
                        )
                    )
                );
            }
            throw ex;
        } finally {
            thread.interrupt();
        }
        if (!file.delete()) {
            throw new IOException(
                String.format(
                    "The temporary file '%s' could not be deleted after calling the exec method",
                    file.getAbsolutePath()
                )
            );
        }
    }

    /**
     * Read port number from file.
     * @param file The file
     * @return Port number
     * @throws Exception If fails
     */
    private static int port(final File file) throws Exception {
        while (!file.exists()) {
            TimeUnit.MILLISECONDS.sleep(1L);
        }
        final int port;
        try (InputStream input = Files.newInputStream(file.toPath())) {
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
    public interface Script {
        /**
         * Execute it against this URI.
         * @param home URI of the running front
         * @throws IOException If fails
         */
        void exec(URI home) throws IOException;
    }

    /**
     * Runnable main method.
     *
     * @since 0.32.5
     */
    private static final class MainMethod implements Runnable {

        /**
         * Method.
         */
        private final Method method;

        /**
         * Additional arguments.
         */
        private final String[] passed;

        /**
         * Ctor.
         * @param method Main method
         * @param passed Additional arguments to be passed to the main method
         */
        MainMethod(final Method method, final String... passed) {
            this.method = method;
            this.passed = Arrays.copyOf(passed, passed.length);
        }

        @Override
        public void run() {
            try {
                this.method.invoke(null, (Object) this.passed);
            } catch (final InvocationTargetException ex) {
                throw new IllegalStateException(
                    String.format(
                        "The %s method has been invoked at an illegal time.",
                        this.method.getName()
                    ), ex
                );
            } catch (final IllegalAccessException ex) {
                throw new IllegalStateException(
                    String.format(
                        "The visibility of the %s method do not allow access.",
                        this.method.getName()
                    ), ex
                );
            }
        }
    }
}
