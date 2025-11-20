/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.IOException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;
import org.takes.Take;
import org.takes.rq.RqWithHeader;

/**
 * Front with a command-line interface.
 *
 * <p>You must provide a {@code --port} argument. Without it, the
 * server won't start. If you want to start the server at a random port, you
 * should specify a file name as the value of this {@code --port} configuration
 * option. For example:</p>
 *
 * <pre> new FtCli(
 *   new TkText("hello, world!"),
 *   "--port=/tmp/port.txt",
 *   "--threads=1",
 *   "--lifetime=3000"
 * ).start(Exit.NEVER);</pre>
 *
 * <p>The code above will start a server and will never stop it. It will
 * work in the foreground. The server will be started at a random TCP
 * port and its number will be saved to the {@code /tmp/port.txt} file.</p>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class FtCli implements Front {

    /**
     * Take.
     */
    private final Take take;

    /**
     * Command line args.
     */
    private final Options options;

    /**
     * Ctor.
     * @param tks Take
     * @param args Arguments
     */
    public FtCli(final Take tks, final String... args) {
        this(tks, Arrays.asList(args));
    }

    /**
     * Ctor.
     * @param tks Take
     * @param args Arguments
     */
    public FtCli(final Take tks, final Iterable<String> args) {
        this.take = tks;
        this.options = new Options(args);
    }

    @Override
    public void start(final Exit exit) throws IOException {
        final Take tks;
        if (this.options.hitRefresh()) {
            tks = request -> this.take.act(
                new RqWithHeader(
                    request, "X-Takes-HitRefresh: yes"
                )
            );
        } else {
            tks = this.take;
        }
        final BkTimeable timeable = new BkTimeable(
            new BkSafe(new BkBasic(tks)),
            this.options.maxLatency()
        );
        timeable.setDaemon(true);
        timeable.start();
        final Front front = new FtBasic(
            new BkParallel(
                timeable,
                this.options.threads()
            ),
            this.options.socket()
        );
        if (this.options.isDaemon()) {
            final Thread thread = new Thread(
                () -> {
                    try {
                        front.start(this.exit(exit));
                    } catch (final IOException ex) {
                        throw new IllegalStateException(
                            "Failed to start the front",
                            ex
                        );
                    }
                }
            );
            thread.setDaemon(true);
            thread.start();
        } else {
            front.start(this.exit(exit));
        }
    }

    /**
     * Create exit.
     * @param exit Original exit
     * @return New exit
     */
    private Exit exit(final Exit exit) {
        final long start = System.currentTimeMillis();
        final long max = this.options.lifetime();
        return new Exit.Or(
            exit,
            new Lifetime(start, max)
        );
    }

    /**
     * Lifetime exceeded exit.
     *
     * @since 0.32.5
     */
    private static final class Lifetime implements Exit {

        /**
         * Start time.
         */
        private final long start;

        /**
         * Max lifetime.
         */
        private final long max;

        /**
         * Ctor.
         * @param start Start time
         * @param max Max lifetime
         */
        Lifetime(final long start, final long max) {
            this.start = start;
            this.max = max;
        }

        @Override
        public boolean ready() {
            return System.currentTimeMillis() - this.start > this.max;
        }
    }
}
