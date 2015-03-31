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
package org.takes.facets.flash;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.EqualsAndHashCode;
import org.takes.rs.RsWithCookie;
import org.takes.rs.RsWrap;

/**
 * Forwarding response.
 *
 * <p>This class helps you to automate flash message mechanism, by
 * adding flash messages to your responses, for example:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act() {
 *     // save the post to the database
 *     return new RsFlash(
 *       new RsForward(),
 *       "thanks for the post"
 *     );
 *   }
 * }</pre>
 *
 * <p>This decorator will add the
 * required "Set-Cookie" header to the response. This is all it is doing.
 * The response is added to the cookie in URL-encoded format, together
 * with the logging level. Flash messages could be of different severity,
 * we're using Java logging levels for that, for example:
 *
 * <pre>public final class TkDiscussion implements Take {
 *   &#64;Override
 *   public Response act() {
 *     if (failed) {
 *       throw new RsFlash(
 *         new RsForward(),
 *         "can't save your post, sorry",
 *         java.util.logging.Level.SEVERE
 *       );
 *     }
 *   }
 * }</pre>
 *
 * <p>This is how the HTTP response will look like (simplified):
 *
 * <pre> HTTP/1.1 303 See Other
 * Set-Cookie: RsFlash=can%27t%20save%20your%20post%2C%20sorry/SEVERE</pre>
 *
 * <p>Here, the name of the cookie is {@code RsFlash}. You can change this
 * default name using a constructor of {@link org.takes.facets.flash.RsFlash},
 * but it's not recommended. It's better to use the default name.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class RsFlash extends RsWrap {

    /**
     * Ctor.
     * @param msg Message to show
     */
    public RsFlash(final String msg) {
        this(msg, Level.INFO);
    }

    /**
     * Ctor.
     * @param err Error
     */
    public RsFlash(final Throwable err) {
        this(err.getLocalizedMessage(), Level.SEVERE);
    }

    /**
     * Ctor.
     * @param msg Message
     * @param level Level
     */
    public RsFlash(final String msg, final Level level) {
        this(msg, level, RsFlash.class.getSimpleName());
    }

    /**
     * Ctor.
     * @param msg Message
     * @param level Level
     * @param cookie Cookie name
     */
    public RsFlash(final String msg, final Level level, final String cookie) {
        super(
            new RsWithCookie(
                cookie, msg,
                "Path=/",
                String.format(
                    Locale.ENGLISH,
                    "Expires=%1$ta, %1$td %1$tb %1$tY %1$tT GMT",
                    new Date(
                        System.currentTimeMillis()
                            + TimeUnit.HOURS.toMillis(1L)
                    )
            )
        )
        );
        assert level != null;
    }

}
