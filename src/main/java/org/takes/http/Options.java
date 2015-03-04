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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;

/**
 * Command line options.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.2
 */
@EqualsAndHashCode(of = "map")
final class Options {

    /**
     * Map of arguments and their values.
     */
    private final transient Map<String, String> map =
        new HashMap<String, String>(0);

    /**
     * Ctor.
     * @param args Arguments
     */
    Options(final Iterable<String> args) {
        final Pattern ptn = Pattern.compile("--([a-z-]+)=(.+)");
        for (final String arg : args) {
            final Matcher matcher = ptn.matcher(arg);
            if (!matcher.matches()) {
                throw new IllegalStateException(
                    String.format("can't parse this argument: '%s'", arg)
                );
            }
            this.map.put(matcher.group(1), matcher.group(2));
        }
    }

    /**
     * Is it a daemon?
     * @return TRUE if yes
     */
    public boolean isDaemon() {
        return this.map.get("daemon") != null;
    }

    /**
     * Get the port to listen to.
     * @return Port number
     */
    public int port() {
        final String port = this.map.get("port");
        if (port == null) {
            throw new IllegalArgumentException("--port must be specified");
        }
        return Integer.parseInt(port);
    }

    /**
     * Get the lifetime in milliseconds.
     * @return Port number
     */
    public long lifetime() {
        final String values = this.map.get("lifetime");
        final long msec;
        if (values == null) {
            msec = Long.MAX_VALUE;
        } else {
            msec = Long.parseLong(values);
        }
        return msec;
    }

}
