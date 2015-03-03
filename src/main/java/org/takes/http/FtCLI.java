/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.takes.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Takes;

/**
 * Front with a command line interface.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "takes", "args" })
public final class FtCLI implements Front {

    /**
     * Takes.
     */
    private final transient Takes takes;

    /**
     * Command line args.
     */
    private final transient Iterable<String> args;

    /**
     * Ctor.
     * @param tks Takes
     * @param ags Arguments
     */
    public FtCLI(final Takes tks, final String... ags) {
        this(tks, Arrays.asList(ags));
    }

    /**
     * Ctor.
     * @param tks Takes
     * @param ags Arguments
     */
    public FtCLI(final Takes tks, final Iterable<String> ags) {
        this.takes = tks;
        this.args = ags;
    }

    @Override
    @SuppressWarnings("PMD.DoNotUseThreads")
    public void start(final Exit exit) throws IOException {
        final Map<String, String> map = this.params();
        final String port = map.get("port");
        if (port == null) {
            throw new IllegalArgumentException("--port must be specified");
        }
        final Front front = new FtBasic(this.takes, Integer.parseInt(port));
        final Exit ext = FtCLI.exit(map, exit);
        if (map.get("daemon") == null) {
            front.start(ext);
        } else {
            final Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            front.start(ext);
                        } catch (final IOException ex) {
                            throw new IllegalStateException(ex);
                        }
                    }
                }
            );
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Create exit.
     * @param params Params
     * @param exit Original exit
     * @return New exit
     */
    private static Exit exit(final Map<String, String> params,
        final Exit exit) {
        final String lifetime = params.get("lifetime");
        final Exit custom;
        if (lifetime == null) {
            custom = Exit.NEVER;
        } else {
            final long start = System.currentTimeMillis();
            final long max = Long.parseLong(lifetime);
            custom = new Exit() {
                @Override
                public boolean ready() {
                    return System.currentTimeMillis() - start > max;
                }
            };
        }
        return new Exit() {
            @Override
            public boolean ready() {
                return exit.ready() || custom.ready();
            }
        };
    }

    /**
     * Parse all params.
     * @return Map of params
     */
    private Map<String, String> params() {
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<String, String>(0);
        final Pattern ptn = Pattern.compile("--([a-z-]+)=(.+)");
        for (final String arg : this.args) {
            final Matcher matcher = ptn.matcher(arg);
            if (!matcher.matches()) {
                throw new IllegalStateException(
                    String.format("can't parse this argument: '%s'", arg)
                );
            }
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }

}
