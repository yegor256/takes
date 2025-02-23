/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.cactoos.io.ReaderOf;
import org.cactoos.io.WriterTo;

/**
 * Command line options.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.2
 */
@EqualsAndHashCode
final class Options {

    /**
     * Map of arguments and their values.
     */
    private final Map<String, String> map;

    /**
     * Constructs an {@code Options} with the specified arguments.
     * @param args Arguments
     * @since 0.9
     */
    Options(final String... args) {
        this(Arrays.asList(args));
    }

    /**
     * Constructs an {@code Options} with the specified arguments.
     * @param args Arguments
     */
    Options(final Iterable<String> args) {
        this.map = Options.asMap(args);
    }

    /**
     * Is it a daemon?
     * @return TRUE if yes
     */
    public boolean isDaemon() {
        return this.map.containsKey("daemon");
    }

    /**
     * Get the socket to listen to.
     * @return Socket
     * @throws IOException If fails
     */
    public ServerSocket socket() throws IOException {
        final String port = this.map.get("port");
        if (port == null) {
            throw new IllegalArgumentException("--port must be specified");
        }
        final ServerSocket socket;
        if (port.matches("\\d+")) {
            socket = new ServerSocket(Integer.parseInt(port));
        } else {
            final File file = new File(port);
            if (file.exists()) {
                try (Reader reader = new ReaderOf(file.toPath())) {
                    final char[] chars = new char[8];
                    final int length = reader.read(chars);
                    socket = new ServerSocket(
                        Integer.parseInt(new String(chars, 0, length))
                    );
                }
            } else {
                socket = new ServerSocket(0);
                try (Writer writer = new WriterTo(file.toPath())) {
                    writer.append(Integer.toString(socket.getLocalPort()));
                }
            }
        }
        return socket;
    }

    /**
     * Are we in hit-refresh mode?
     * @return TRUE if this mode is ON
     * @since 0.9
     */
    public boolean hitRefresh() {
        return this.map.containsKey("hit-refresh");
    }

    /**
     * Get the lifetime in milliseconds.
     * @return Port number
     */
    public long lifetime() {
        return Long.parseLong(
            this.map.getOrDefault(
                "lifetime", String.valueOf(Long.MAX_VALUE)
            )
        );
    }

    /**
     * Get the threads.
     * @return Threads
     */
    public int threads() {
        return Integer.parseInt(
            this.map.getOrDefault(
                "threads",
                String.valueOf(Runtime.getRuntime().availableProcessors() << 2)
            )
        );
    }

    /**
     * Get the max latency in milliseconds.
     * @return Latency
     */
    public long maxLatency() {
        return Long.parseLong(
            this.map.getOrDefault(
                "max-latency",
                String.valueOf(Long.MAX_VALUE)
            )
        );
    }

    /**
     * Convert the provided arguments into a Map.
     * @param args Arguments to parse.
     * @return Map A map containing all the arguments and their values.
     * @throws IllegalStateException If an argument doesn't match with the
     *  expected format which is {@code --([a-z\-]+)(=.+)?}.
     */
    private static Map<String, String> asMap(final Iterable<String> args) {
        final Map<String, String> map = new HashMap<>(0);
        final Pattern ptn = Pattern.compile("--([a-z\\-]+)(=.+)?");
        for (final String arg : args) {
            final Matcher matcher = ptn.matcher(arg);
            if (!matcher.matches()) {
                throw new IllegalStateException(
                    String.format("Can't parse this argument: '%s'", arg)
                );
            }
            final String value = matcher.group(2);
            if (value == null) {
                map.put(matcher.group(1), "");
            } else {
                map.put(matcher.group(1), value.substring(1));
            }
        }
        return map;
    }
}
