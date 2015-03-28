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
public class LogWrap {
    /**
     * Log levels.
     */
    public static enum Level { TRACE, DEBUG, INFO, WARN, SEVERE };

    /**
     * Log level.
     */
    private final transient LogWrap.Level level;

    /**
     * Logger.
     */
    private Logger logger;
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
    // @checkstyle JavadocLocationCheck (1 line)
    // @checkstyle CyclomaticComplexityCheck (1 line)
    public final void log(final String format, final Object... param) {
        if (this.level == LogWrap.Level.TRACE && this.logger.isTraceEnabled()) {
            this.logger.trace(String.format(format, param));
        } else if (
            this.level == LogWrap.Level.DEBUG
                && this.logger.isDebugEnabled()) {
            this.logger.debug(String.format(format, param));
        } else if (
            this.level == LogWrap.Level.INFO
                && this.logger.isInfoEnabled()) {
            this.logger.trace(String.format(format, param));
        } else if (
            this.level == LogWrap.Level.WARN
                && this.logger.isWarnEnabled()) {
            this.logger.warn(String.format(format, param));
        } else if (
            this.level == LogWrap.Level.SEVERE
                && this.logger.isErrorEnabled()) {
            this.logger.error(String.format(format, param));
        }
    }
}
