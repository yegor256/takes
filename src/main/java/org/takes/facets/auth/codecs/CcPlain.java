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
package org.takes.facets.auth.codecs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;
import org.takes.misc.Utf8String;

/**
 * Plain codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode
public final class CcPlain implements Codec {

    @Override
    public byte[] encode(final Identity identity) throws IOException {
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
        return new Utf8String(text.toString()).bytes();
    }

    @Override
    public Identity decode(final byte[] bytes) throws IOException {
        final String[] parts = new Utf8String(bytes).string().split(";");
        final Map<String, String> map = new HashMap<>(parts.length);
        for (int idx = 1; idx < parts.length; ++idx) {
            final String[] pair = parts[idx].split("=");
            try {
                map.put(pair[0], CcPlain.decode(pair[1]));
            } catch (final IllegalArgumentException ex) {
                throw new DecodingException(ex);
            }
        }
        return new Identity.Simple(CcPlain.decode(parts[0]), map);
    }

    /**
     * Decode from URL.
     * @param text The text
     * @return Decoded
     * @throws UnsupportedEncodingException If fails
     */
    private static String decode(final String text)
        throws UnsupportedEncodingException {
        try {
            return URLDecoder.decode(text, Charset.defaultCharset().name());
        } catch (final IllegalArgumentException ex) {
            throw new DecodingException(ex);
        }
    }

}
