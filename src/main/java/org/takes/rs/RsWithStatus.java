/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;
import org.takes.misc.Concat;
import org.takes.misc.Condition;
import org.takes.misc.Select;

/**
 * Response decorator, with status code.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithStatus extends RsWrap {

    /**
     * Statuses and their reasons.
     */
    private static final Map<Integer, String> REASONS =
        Collections.unmodifiableMap(RsWithStatus.make());

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
    public RsWithStatus(final Response res, final int code,
        final CharSequence rsn) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return RsWithStatus.head(res, code, rsn);
                }
                @Override
                public InputStream body() throws IOException {
                    return res.body();
                }
            }
        );
    }

    /**
     * Make head.
     * @param origin Original response
     * @param status Status
     * @param reason Reason
     * @return Head
     * @throws IOException If fails
     */
    private static Iterable<String> head(final Response origin,
        final int status, final CharSequence reason) throws IOException {
        // @checkstyle MagicNumber (1 line)
        if (status < 100 || status > 999) {
            throw new IllegalArgumentException(
                String.format(
                    // @checkstyle LineLength (1 line)
                    "according to RFC 7230 HTTP status code must have three digits: %d",
                    status
                )
            );
        }
        return new Concat<String>(
            Collections.singletonList(
                String.format("HTTP/1.1 %d %s", status, reason)
            ),
            new Select<String>(
                origin.head(),
                new Condition<String>() {
                    @Override
                    public boolean fits(final String item) {
                        return !item.startsWith("HTTP/");
                    }
                }
            )
        );
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
     * @checkstyle ExecutableStatementCountCheck (100 lines)
     */
    private static Map<Integer, String> make() {
        final Map<Integer, String> map = new HashMap<>(0);
        map.put(HttpURLConnection.HTTP_OK, "OK");
        map.put(HttpURLConnection.HTTP_NO_CONTENT, "No Content");
        map.put(HttpURLConnection.HTTP_CREATED, "Created");
        map.put(HttpURLConnection.HTTP_ACCEPTED, "Accepted");
        map.put(HttpURLConnection.HTTP_MOVED_PERM, "Moved Permanently");
        map.put(HttpURLConnection.HTTP_MOVED_TEMP, "Moved Temporarily");
        map.put(HttpURLConnection.HTTP_SEE_OTHER, "See Other");
        map.put(HttpURLConnection.HTTP_NOT_MODIFIED, "Not Modified");
        map.put(HttpURLConnection.HTTP_USE_PROXY, "Use Proxy");
        map.put(HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
        map.put(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
        map.put(HttpURLConnection.HTTP_PAYMENT_REQUIRED, "Payment Required");
        map.put(HttpURLConnection.HTTP_FORBIDDEN, "Forbidden");
        map.put(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
        map.put(HttpURLConnection.HTTP_BAD_METHOD, "Bad Method");
        map.put(HttpURLConnection.HTTP_NOT_ACCEPTABLE, "Not Acceptable");
        map.put(HttpURLConnection.HTTP_CLIENT_TIMEOUT, "Client Timeout");
        map.put(HttpURLConnection.HTTP_GONE, "Gone");
        map.put(HttpURLConnection.HTTP_LENGTH_REQUIRED, "Length Required");
        map.put(HttpURLConnection.HTTP_PRECON_FAILED, "Precon Failed");
        map.put(HttpURLConnection.HTTP_ENTITY_TOO_LARGE, "Entity Too Large");
        map.put(HttpURLConnection.HTTP_REQ_TOO_LONG, "Request Too Long");
        map.put(HttpURLConnection.HTTP_UNSUPPORTED_TYPE, "Unsupported Type");
        map.put(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Error");
        map.put(HttpURLConnection.HTTP_BAD_GATEWAY, "Bad Gateway");
        map.put(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "Not Implemented");
        return map;
    }

}
