/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
