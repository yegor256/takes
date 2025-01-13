/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
package org.takes.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.concurrent.atomic.AtomicReference;
import org.takes.HttpException;
import org.takes.Take;

/**
 * Servlet for take.
 *
 * @since 2.0
 * @todo #953:30min Integration with Servlets Session API,
 *  see https://github.com/yegor256/takes/pull/865 discussion for details.
 *  Add support and tests to Servlet Session. This implementation will
 *  able store data in the server side, as a session does.
 */
public final class SrvTake extends HttpServlet {
    /**
     * Id for serializable.
     */
    private static final long serialVersionUID = -8119918127398448635L;

    /**
     * Take, initialize in {@link #init()}.
     */
    private final AtomicReference<Take> tke;

    /**
     * Ctor.
     */
    public SrvTake() {
        super();
        this.tke = new AtomicReference<>();
    }

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public void init() throws ServletException {
        super.init();
        final String cname = this.getServletConfig()
            .getInitParameter("take");
        final Class<?> cls;
        try {
            cls = Class.forName(cname);
        } catch (final ClassNotFoundException err) {
            throw new ServletException(
                String.format(
                    "Class %s was not found",
                    cname
                ),
                err
            );
        }
        final Take take;
        try {
            take = (Take) cls.getConstructor(ServletContext.class)
                .newInstance(this.getServletContext());
        } catch (final InstantiationException err) {
            throw new ServletException(
                String.format(
                    "Can't construct %s class",
                    cls.getCanonicalName()
                ),
                err
            );
        } catch (final IllegalAccessException err) {
            throw new ServletException(
                String.format(
                    "Constructor %s(ServletContext) is private",
                    cls.getCanonicalName()
                ),
                err
            );
        } catch (final InvocationTargetException err) {
            throw new ServletException(
                String.format(
                    "Error during instantiating %s",
                    cls.getCanonicalName()
                ),
                err
            );
        } catch (final NoSuchMethodException err) {
            throw new ServletException(
                String.format(
                    "Constructor %s(ServletContext) was not found",
                    cls.getCanonicalName()
                ),
                err
            );
        }
        if (!this.tke.compareAndSet(null, take)) {
            throw new IllegalStateException(
                "Take is already constructed"
            );
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public void service(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws IOException {
        try {
            new ResponseOf(this.tke.get().act(new RqFrom(req)))
                .applyTo(resp);
        } catch (final HttpException err) {
            resp.sendError(err.code(), err.getMessage());
            //@checkstyle IllegalCatch (1 line)
        } catch (final Exception ignored) {
            resp.sendError(
                HttpURLConnection.HTTP_INTERNAL_ERROR,
                "Internal error"
            );
        }
    }
}
