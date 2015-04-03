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

package org.takes.facets.slf4j;

import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Slf4j target.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11.2
 */
@EqualsAndHashCode(of = "logger")
@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
final class Slf4j implements Target {
    /**
     * Log level.
     */
    private final transient Level level;

    /**
     * Logger.
     */
    private transient Logger logger;

    /**
     * Ctor.
     * @param clazz Logger class
     */
    Slf4j(final Class<?> clazz) {
        this(clazz.getName());
    }

    /**
     * Ctor.
     * @param name Logger name
     */
    Slf4j(final String name) {
        this(name, Level.FINE);
    }

    /**
     * Ctor.
     * @param clazz Logger class
     * @param lvl Log level
     */
    Slf4j(final Class<?> clazz, final Level lvl) {
        this(clazz.getName(), lvl);
    }

    /**
     * Ctor.
     * @param name Logger name
     * @param lvl Log level
     */
    Slf4j(final String name, final Level lvl) {
        this.level = lvl;
        this.logger = LoggerFactory.getLogger(name);
    }

    /**
     * Log message.
     * @param format Format string
     * @param param Parameters
     */
    @Override
    public void log(final String format, final Object... param) {
        if (Level.FINEST.equals(this.level)) {
            this.logger.trace(format, param);
        } else if (Level.FINE.equals(this.level)) {
            this.logger.debug(format, param);
        } else if (Level.INFO.equals(this.level)) {
            this.logger.info(format, param);
        } else if (Level.WARNING.equals(this.level)) {
            this.logger.warn(format, param);
        } else if (Level.SEVERE.equals(this.level)) {
            this.logger.error(format, param);
        } else {
            throw new IllegalArgumentException("Unknown log level");
        }
    }
}
