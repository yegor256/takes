/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
 * Servlet adapter for Takes framework.
 *
 * <p>This servlet allows Takes applications to run inside any servlet
 * container (Tomcat, Jetty, etc.) by bridging between the servlet API
 * and Takes' {@link Take} interface. It acts as an entry point that
 * receives servlet requests and delegates them to a Takes application
 * for processing.
 *
 * <p>The servlet is configured through the standard servlet initialization
 * parameter "take" which should specify the fully qualified class name
 * of the {@link Take} implementation to use. The Take class must have
 * either a no-argument constructor or a constructor that accepts a
 * {@link ServletContext}.
 *
 * <p>Configuration example in web.xml:
 * <pre>{@code
 * &lt;servlet>
 *   &lt;servlet-name>app&lt;/servlet-name>
 *   &lt;servlet-class>org.takes.servlet.SrvTake&lt;/servlet-class>
 *   &lt;init-param>
 *     &lt;param-name>take&lt;/param-name>
 *     &lt;param-value>com.example.MyTake&lt;/param-value>
 *   &lt;/init-param>
 * &lt;/servlet>
 * }</pre>
 *
 * <p>The servlet handles the complete request/response lifecycle:
 * <ul>
 *   <li>Converts {@link HttpServletRequest} to Takes {@link org.takes.Request}</li>
 *   <li>Processes the request through the configured {@link Take}</li>
 *   <li>Converts Takes {@link org.takes.Response} back to {@link HttpServletResponse}</li>
 *   <li>Handles exceptions and maps them to appropriate HTTP status codes</li>
 * </ul>
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
     * Take, initialized in {@link #init()}.
     */
    private final AtomicReference<Take> that;

    /**
     * Ctor.
     */
    public SrvTake() {
        super();
        this.that = new AtomicReference<>();
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
        if (!this.that.compareAndSet(null, take)) {
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
            new ResponseOf(this.that.get().act(new RqFrom(req)))
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
