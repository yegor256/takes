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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.EqualsAndHashCode;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.takes.Response;

/**
 * Response that converts Velocity template to text.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = { "template", "params" })
public final class RsVelocity implements Response {

    /**
     * Template.
     */
    private final transient InputStream template;

    /**
     * Params.
     */
    private final transient Map<String, Object> params;

    /**
     * Ctor.
     * @param tpl Template
     */
    public RsVelocity(final String tpl) {
        this(new ByteArrayInputStream(tpl.getBytes()));
    }

    /**
     * Ctor.
     * @param tpl Template
     * @throws IOException If fails
     */
    public RsVelocity(final URL tpl) throws IOException {
        this(tpl.openStream());
    }

    /**
     * Ctor.
     * @param tpl Template
     */
    public RsVelocity(final InputStream tpl) {
        this(tpl, new HashMap<String, Object>(0));
    }

    /**
     * Ctor.
     * @param tpl Template
     * @param map Map of params
     */
    public RsVelocity(final InputStream tpl, final Map<String, Object> map) {
        this.template = tpl;
        this.params = Collections.unmodifiableMap(map);
    }

    /**
     * With this parameter.
     * @param key Key
     * @param value Value
     * @return Response
     */
    public RsVelocity with(final String key, final Object value) {
        final ConcurrentMap<String, Object> map =
            new ConcurrentHashMap<String, Object>(this.params.size() + 1);
        map.putAll(this.params);
        map.put(key, value);
        return new RsVelocity(this.template, map);
    }

    @Override
    public Iterable<String> head() {
        return new RsEmpty().head();
    }

    @Override
    public InputStream body() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(baos);
        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(
            RuntimeConstants.RUNTIME_LOG_LOGSYSTEM,
            new NullLogChute()
        );
        engine.evaluate(
            new VelocityContext(this.params),
            writer,
            "",
            new InputStreamReader(this.template)
        );
        writer.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }

}
