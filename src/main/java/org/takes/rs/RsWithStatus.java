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
package org.takes.rs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.takes.Response;

/**
 * Response decorator, with status code.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "origin", "status", "reason" })
public final class RsWithStatus implements Response {

    /**
     * Statuses and their reasons.
     */
    private static final Map<Integer, String> REASONS =
        Collections.unmodifiableMap(RsWithStatus.make());

    /**
     * Original response.
     */
    private final transient Response origin;

    /**
     * Status code.
     */
    private final transient int status;

    /**
     * Reason.
     */
    private final transient String reason;

    /**
     * Ctor.
     * @param code Status code
     */
    public RsWithStatus(final int code) {
        this(new RsEmpty(), code);
    }

    /**
     * Ctor.
     * @param res Original response
     * @param code Status code
     */
    public RsWithStatus(final Response res, final int code) {
        this(res, code, RsWithStatus.best(code));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param code Status code
     * @param rsn Reason
     */
    public RsWithStatus(final Response res, final int code, final String rsn) {
        // @checkstyle MagicNumber (1 line)
        if (code < 100 || code > 999) {
            throw new IllegalArgumentException(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "according to RFC 7230 HTTP status code must have three digits: %d",
                    code
                )
            );
        }
        this.origin = res;
        this.status = code;
        this.reason = rsn;
    }

    @Override
    public List<String> head() throws IOException {
        final List<String> list = this.origin.head();
        final List<String> head = new ArrayList<String>(list.size());
        head.add(String.format("HTTP/1.1 %d %s", this.status, this.reason));
        head.addAll(list.subList(1, list.size()));
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }

    /**
     * Find the best reason for this status code.
     * @param code The code
     * @return Reason
     */
    private static String best(final int code) {
        String reason = RsWithStatus.REASONS.get(code);
        if (reason == null) {
            reason = "Unknown";
        }
        return reason;
    }

    /**
     * Make all reasons.
     * @return Map of status codes and reasons
     */
    private static Map<Integer, String> make() {
        final ConcurrentMap<Integer, String> map =
            new ConcurrentHashMap<Integer, String>(0);
        map.put(HttpURLConnection.HTTP_OK, "OK");
        map.put(HttpURLConnection.HTTP_NO_CONTENT, "No Content");
        map.put(HttpURLConnection.HTTP_CREATED, "Created");
        map.put(HttpURLConnection.HTTP_ACCEPTED, "Accepted");
        map.put(HttpURLConnection.HTTP_MOVED_PERM, "Moved Permanently");
        map.put(HttpURLConnection.HTTP_MOVED_TEMP, "Moved Temporarily");
        map.put(HttpURLConnection.HTTP_SEE_OTHER, "See Other");
        map.put(HttpURLConnection.HTTP_NOT_MODIFIED, "Not Modified");
        map.put(HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
        map.put(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
        map.put(HttpURLConnection.HTTP_PAYMENT_REQUIRED, "Payment Required");
        map.put(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
        map.put(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
        map.put(HttpURLConnection.HTTP_BAD_METHOD, "Bad Method");
        map.put(HttpURLConnection.HTTP_BAD_GATEWAY, "Bad Gateway");
        map.put(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Error");
        return map;
    }

}
