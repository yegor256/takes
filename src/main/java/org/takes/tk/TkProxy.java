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
package org.takes.tk;

import com.jcabi.http.request.JdkRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqHref;
import org.takes.rq.RqLengthAware;
import org.takes.rq.RqMethod;
import org.takes.rq.RqPrint;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithHeaders;
import org.takes.rs.RsWithStatus;

/**
 * Take that proxies requests to another destination.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkProxy implements Take {

    /**
     * Target host to which requests are forwarded.
     */
    private final transient String host;

    /**
     * Ctor.
     *
     * <p>The {@code target} parameter takes the destination URL
     *  to which requests are proxied. Some valid examples:
     *  <li>example.com
     *  <li>www.example.com
     *  <li>example.com:8080
     *  <li>example.com/x/y
     * @param target Target to which requests are forwarded
     */
    public TkProxy(final String target) {
        this.host = target;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final RqHref.Base base = new RqHref.Base(req);
        final String home = new RqHref.Smart(base).home().bare();
        final String dest = String.format(
            "http://%s/%s",
            this.host,
            base.href().toString().substring(home.length())
        );
        return this.response(home, dest, this.request(req, dest).fetch());
    }

    /**
     * Creates the request to be forwarded to the target host.
     *
     * @param req Original request
     * @param dest Destination URL
     * @return Request to be forwarded
     * @throws IOException If some problem inside
     */
    private com.jcabi.http.Request request(final Request req,
        final String dest) throws IOException {
        final String method = new RqMethod.Base(req).method();
        com.jcabi.http.Request proxied = new JdkRequest(dest)
            .method(method);
        final RqHeaders.Base headers = new RqHeaders.Base(req);
        for (final String name : headers.names()) {
            if ("content-length".equals(name.toLowerCase(Locale.ENGLISH))) {
                continue;
            }
            if (isHost(name)) {
                proxied = proxied.header(name, this.host);
                continue;
            }
            for (final String value : headers.header(name)) {
                proxied = proxied.header(name, value);
            }
        }
        if (headers.header("Content-Length").iterator().hasNext()
            || headers.header("Transfer-Encoding").iterator().hasNext()) {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();
            new RqPrint(new RqLengthAware(req)).printBody(output);
            proxied = proxied.body().set(output.toByteArray()).back();
        }
        return proxied;
    }

    /**
     * Creates the response received from the target host.
     *
     * @param home Home host
     * @param dest Destination URL
     * @param rsp Response received from the target host
     * @return Response
     */
    private Response response(final String home, final String dest,
        final com.jcabi.http.Response rsp) {
        final Collection<String> hdrs = new LinkedList<String>();
        hdrs.add(
            String.format(
                "X-Takes-TkProxy: from %s to %s",
                home, dest
            )
        );
        for (final Map.Entry<String, List<String>> entry
            : rsp.headers().entrySet()) {
            for (final String value : entry.getValue()) {
                final String val;
                if (isHost(entry.getKey())) {
                    val = this.host;
                } else {
                    val = value;
                }
                hdrs.add(String.format("%s: %s", entry.getKey(), val));
            }
        }
        return new RsWithStatus(
            new RsWithBody(
                new RsWithHeaders(hdrs),
                rsp.binary()
            ),
            rsp.status(),
            rsp.reason()
        );
    }

    /**
     * Checks whether the provided argument is a "Host" header name.
     * @param header Header name
     * @return Returns {@code true} if {@code header} parameter is a "Host"
     *  header name, {@code false} otherwise
     */
    private static boolean isHost(final String header) {
        return "host".equals(header.toLowerCase(Locale.ENGLISH));
    }
}
