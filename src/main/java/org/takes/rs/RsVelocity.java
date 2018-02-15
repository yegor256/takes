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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.takes.Response;
import org.takes.misc.Utf8InputStreamReader;
import org.takes.misc.Utf8OutputStreamWriter;
import org.takes.misc.Utf8String;

/**
 * Response that converts Velocity template to text.
 *
 * <p>This response implementation is rendering a page from
 * Apache Velocity template. Here is how you can use it:
 *
 * <pre>public final class TkHelp implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     return new RsHTML(
 *       new RsVelocity(
 *         this.getClass().getResource("help.html.vm"),
 *         new RsVelocity.Pair("name", "Jeffrey")
 *       )
 *     );
 *   }
 * }</pre>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsVelocity extends RsWrap {

    /**
     * Ctor.
     * @param template Template
     * @param params List of params
     * @since 0.11
     */
    public RsVelocity(final CharSequence template,
        final RsVelocity.Pair... params) {
        this(
            new ByteArrayInputStream(
                new Utf8String(template.toString()).bytes()
            ), params
        );
    }

    /**
     * Ctor.
     * @param template Template
     * @param params List of params
     * @throws IOException If fails
     * @since 0.11
     */
    public RsVelocity(final URL template,
        final RsVelocity.Pair... params) throws IOException {
        this(template.openStream(), params);
    }

    /**
     * Ctor.
     * @param template Template
     * @param params Entries
     */
    public RsVelocity(final InputStream template,
        final RsVelocity.Pair... params) {
        this(template, RsVelocity.asMap(params));
    }

    /**
     * Ctor.
     * @param template Template
     * @param params Map of params
     */
    public RsVelocity(final InputStream template, final Map<CharSequence,
        Object> params) {
        this(".", template, params);
    }
    /**
     * Ctor.
     * @param folder Template folder
     * @param template Template
     * @param params Map of params
     */
    public RsVelocity(final String folder,
        final InputStream template, final Map<CharSequence, Object> params) {
        super(
            new Response() {
                @Override
                public Iterable<String> head() {
                    return new RsEmpty().head();
                }
                @Override
                public InputStream body() throws IOException {
                    return RsVelocity.render(folder, template, params);
                }
            }
        );
    }

    /**
     * Render it.
     * @param folder Template folder
     * @param template Page template
     * @param params Params for velocity
     * @return Page body
     * @throws IOException If fails
     */
    private static InputStream render(final String folder,
        final InputStream template,
        final Map<CharSequence, Object> params) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Writer writer = new Utf8OutputStreamWriter(baos);
        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(
            RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,
            new NullLogChute()
        );
        engine.setProperty(
            "file.resource.loader.path",
            folder
        );
        engine.evaluate(
            new VelocityContext(params),
            writer,
            "",
            new Utf8InputStreamReader(template)
        );
        writer.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * Convert entries to map.
     * @param entries Entries
     * @return Map
     */
    @SafeVarargs
    private static Map<CharSequence, Object> asMap(
        final Map.Entry<CharSequence, Object>... entries) {
        final Map<CharSequence, Object> map = new HashMap<>(entries.length);
        for (final Map.Entry<CharSequence, Object> ent : entries) {
            map.put(ent.getKey(), ent.getValue());
        }
        return map;
    }

    /**
     * Pair of values.
     */
    public static final class Pair
        extends AbstractMap.SimpleEntry<CharSequence, Object> {
        /**
         * Serialization marker.
         */
        private static final long serialVersionUID = 7362489770169963015L;
        /**
         * Ctor.
         * @param key Key
         * @param obj Pass
         */
        public Pair(final CharSequence key, final Object obj) {
            super(key, obj);
        }
    }

}
