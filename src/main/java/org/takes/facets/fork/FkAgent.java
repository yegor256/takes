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
package org.takes.facets.fork;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.am.AgentMatch;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Fork by user agent criteria accepted by "User-Agent" HTTP header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 1.7.2
 */
@EqualsAndHashCode
public final class FkAgent implements Fork {

    /**
     * Pattern to extract name and version from user-agent.
     */
    private static final Pattern PATTERN =
        Pattern.compile("(\\w+)\\/([\\d\\.]+)");

    /**
     * Take to handle the request and dynamically return the response.
     */
    private final Take take;

    /**
     * User agent matcher.
     */
    private final AgentMatch match;

    /**
     * Ctor.
     * @param take Take to handle the request dynamically.
     * @param match Matcher.
     */
    public FkAgent(final Take take, final AgentMatch match) {
        this.take = take;
        this.match = match;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public Opt<Response> route(final Request req) throws Exception {
        final List<String> tokens = FkAgent.tokens(req);
        Opt<Response> resp = new Opt.Empty<>();
        for (final String token : tokens) {
            if (this.match.matches(token)) {
                resp = new Opt.Single<>(this.take.act(req));
                break;
            }
        }
        return resp;
    }

    /**
     * Extract tokens from request.
     * @param req Request.
     * @return List of user agent tokens.
     * @throws IOException If some problems inside.
     */
    private static List<String> tokens(final Request req) throws IOException {
        final List<String> tokens = new LinkedList<>();
        final Iterable<String> headers =
            new RqHeaders.Base(req).header("User-Agent");
        for (final String header : headers) {
            final Matcher matcher = PATTERN.matcher(header);
            if (matcher.matches()) {
                tokens.add(matcher.group());
            }
        }
        return tokens;
    }
}
