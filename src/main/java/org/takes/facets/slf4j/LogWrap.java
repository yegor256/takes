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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrap slf4j functionality.
 *
 * <p>The class is immutable and thread-safe.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11
 */
@SuppressWarnings("PMD.LoggerIsNotStaticFinal")
public class LogWrap {
    /**
     * Default log level.
     */
    public static final LogWrap.Level DEFAULT_LEVEL = LogWrap.Level.DEBUG;
    /**
     * Log levels.
     */
    public enum Level {
        /**
         * Trace level.
         */
        TRACE,
        /**
         * Debug level.
         */
        DEBUG,
        /**
         * Info level.
         */
        INFO,
        /**
         * Warn level.
         */
        WARN,
        /**
         * Error level.
         */
        ERROR
    };

    /**
     * Log level.
     */
    private final transient LogWrap.Level level;

    /**
     * Logger.
     */
    private final transient Logger logger;

    /**
     * Ctor.
     * @param clazz Logger class
     */
    public LogWrap(final Class<?> clazz) {
        this(clazz, LogWrap.Level.DEBUG);
    }

    /**
     * Ctor.
     * @param clazz Logger class
     * @param lvl Log level
     */
    public LogWrap(final Class<?> clazz, final LogWrap.Level lvl) {
        this.level = lvl;
        this.logger = LoggerFactory.getLogger(clazz);
    }

    /**
     * Log message.
     * @param format Format string
     * @param param Parameters
     */
    public final void log(final String format, final Object... param) {
        switch (this.level) {
            case TRACE :
                this.logger.trace(format, param);
                break;
            case DEBUG:
                this.logger.debug(format, param);
                break;
            case INFO:
                this.logger.info(format, param);
                break;
            case WARN:
                this.logger.warn(format, param);
                break;
            case ERROR:
                this.logger.error(format, param);
                break;
            default:
                throw new IllegalArgumentException("Unknown log level");
        }
    }
}
