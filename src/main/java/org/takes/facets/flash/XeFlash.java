/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.facets.cookies.RqCookies;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * An Xembly source that converts flash messages from cookies into XML elements.
 *
 * <p>This class extracts flash messages from HTTP cookies and converts them into
 * XML elements using Xembly directives. It parses the cookie format (message/level)
 * and creates flash, message, and level XML elements. The class is immutable
 * and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class XeFlash implements XeSource {
    /**
     * Compiled RsFlash message regexp pattern.
     */
    private static final Pattern PTN = Pattern.compile(
        "^(.*?)/(.*?)$"
    );

    /**
     * Request.
     */
    private final Request req;

    /**
     * Cookie name.
     */
    private final String cookie;

    /**
     * Constructor with default cookie name.
     * @param request The HTTP request containing flash cookies
     */
    public XeFlash(final Request request) {
        this(request, RsFlash.class.getSimpleName());
    }

    /**
     * Constructor with custom cookie name.
     * @param request The HTTP request containing flash cookies
     * @param name The name of the flash cookie to look for
     */
    public XeFlash(final Request request, final String name) {
        this.req = request;
        this.cookie = name;
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        final Iterator<String> cookies =
            new RqCookies.Base(this.req).cookie(this.cookie).iterator();
        final Directives dirs = new Directives();
        if (cookies.hasNext()) {
            final Matcher matcher = XeFlash.PTN.matcher(cookies.next());
            if (matcher.find()) {
                dirs.add("flash")
                    .add("message").set(
                        URLDecoder.decode(
                            matcher.group(1),
                            Charset.defaultCharset().name()
                        )
                    ).up()
                    .add("level").set(matcher.group(2));
            }
        }
        return dirs;
    }
}
