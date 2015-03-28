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
package org.takes.facets.fork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.rq.RqHeaders;

/**
 * Fork by encoding accepted by "Accept-Encoding" HTTP header.
 *
 * <p>Use this fork in order to deliver responses with different
 * encoding, depending on user preferences. For example, you want
 * to deliver GZIP-compressed response when "Accept-Encoding" request
 * header contains "gzip". Here is how:
 *
 * <pre> new TsFork(
 *   new FkEncoding("gzip", new RsGzip(response)),
 *   new FkEncoding("", response)
 * )</pre>
 *
 * <p>Empty string as an encoding means that the fork should match
 * in any case.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.10
 * @see org.takes.facets.fork.RsFork
 */
@EqualsAndHashCode(of = { "encoding", "origin" })
public final class FkEncoding implements Fork.AtResponse {

    /**
     * Encoding we can deliver (or empty string).
     */
    private final transient String encoding;

    /**
     * Response to return.
     */
    private final transient Response origin;

    /**
     * Ctor.
     * @param enc Encoding we accept
     * @param response Response to return
     */
    public FkEncoding(final String enc, final Response response) {
        this.encoding = enc.trim().toLowerCase(Locale.ENGLISH);
        this.origin = response;
    }

    @Override
    public Iterator<Response> route(final Request req) throws IOException {
        final Iterator<String> headers =
            new RqHeaders(req).header("Accept-Encoding").iterator();
        final Collection<Response> list = new ArrayList<Response>(1);
        if (this.encoding.isEmpty()) {
            list.add(this.origin);
        } else if (headers.hasNext()) {
            final Collection<String> items = Arrays.asList(
                headers.next().trim()
                    .toLowerCase(Locale.ENGLISH)
                    .split("\\s*,\\s*")
            );
            if (items.contains(this.encoding)) {
                list.add(this.origin);
            }
        }
        return list.iterator();
    }

}
