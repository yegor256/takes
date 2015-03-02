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
package org.takes.f.auth;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;

/**
 * Identity in Base64.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = { "name", "props" })
final class BaseIdentity implements Identity {

    /**
     * URN.
     */
    private final transient String name;

    /**
     * Properties.
     */
    private final transient Map<String, String> props;

    /**
     * Ctor.
     * @param text Identity in text
     * @throws IOException If fails
     */
    BaseIdentity(final String text) throws IOException {
        final String[] parts = text.split(";");
        this.name = parts[0];
        final ConcurrentMap<String, String> map =
            new ConcurrentHashMap<String, String>(parts.length);
        for (int idx = 1; idx < parts.length; ++idx) {
            final String[] pair = parts[idx].split("=");
            map.put(
                pair[0],
                URLDecoder.decode(pair[1], Charset.defaultCharset().name())
            );
        }
        this.props = map;
    }

    /**
     * Ctor.
     * @param origin Original identity
     */
    BaseIdentity(final Identity origin) {
        this(origin.urn(), origin.properties());
    }

    /**
     * Ctor.
     * @param urn URN
     * @param map Properties
     */
    BaseIdentity(final String urn, final Map<String, String> map) {
        this.name = urn;
        this.props = Collections.unmodifiableMap(map);
    }

    /**
     * Convert it to text.
     * @return Text
     */
    public String toText() {
        return "";
    }

    @Override
    public String urn() {
        return this.name;
    }

    @Override
    public Map<String, String> properties() {
        return Collections.unmodifiableMap(this.props);
    }
}
