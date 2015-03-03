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
package org.takes.facets.auth;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;

/**
 * Plain codec.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode
public final class CcPlain implements Codec {

    @Override
    public String encode(final Identity identity) throws IOException {
        final String encoding = Charset.defaultCharset().name();
        final StringBuilder text = new StringBuilder(
            URLEncoder.encode(identity.urn(), encoding)
        );
        for (final Map.Entry<String, String> ent
            : identity.properties().entrySet()) {
            text.append(';')
                .append(ent.getKey())
                .append('=')
                .append(URLEncoder.encode(ent.getValue(), encoding));
        }
        return text.toString();
    }

    @Override
    public Identity decode(final String text) throws IOException {
        final String[] parts = text.split(";");
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<String, String>(parts.length);
        for (int idx = 1; idx < parts.length; ++idx) {
            final String[] pair = parts[idx].split("=");
            map.put(
                pair[0],
                URLDecoder.decode(pair[1], Charset.defaultCharset().name())
            );
        }
        final String urn = URLDecoder.decode(
            parts[0], Charset.defaultCharset().name()
        );
        return new Identity() {
            @Override
            public String urn() {
                return urn;
            }
            @Override
            public Map<String, String> properties() {
                return map;
            }
        };
    }

}
