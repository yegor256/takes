/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.cactoos.Scalar;
import org.cactoos.io.InputStreamOf;
import org.cactoos.io.ReaderOf;
import org.cactoos.io.WriterTo;

/**
 * Response decorator that renders Apache Velocity templates.
 *
 * <p>This decorator processes Apache Velocity templates with provided
 * parameters to generate dynamic content. It supports templates from
 * various sources including strings, URLs, and input streams. Template
 * parameters can be provided as key-value pairs or maps.
 *
 * <p>Example usage:
 * <pre>public final class TkHelp implements Take {
 *   &#64;Override
 *   public Response act(final Request req) {
 *     return new RsHtml(
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
 * @since 0.1
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
            new InputStreamOf(template),
            params
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
        this(folder, template, () -> RsVelocity.convert(params));
    }

    /**
     * Ctor.
     * @param folder Template folder
     * @param template Template
     * @param params Map of params
     */
    public RsVelocity(final String folder,
        final InputStream template, final Scalar<Map<String, Object>> params) {
        super(
            new ResponseOf(
                () -> new RsEmpty().head(),
                () -> RsVelocity.render(folder, template, params.value())
            )
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
        final Map<String, Object> params) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Writer writer = new WriterTo(baos)) {
            final VelocityEngine engine = new VelocityEngine();
            engine.setProperty(
                "resource.loader.file.path",
                folder
            );
            engine.evaluate(
                new VelocityContext(params),
                writer,
                "",
                new ReaderOf(template)
            );
        }
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
     * Converts Map of CharSequence, Object to Map of String, Object.
     * @param params Parameters in Map of CharSequence, Object
     * @return Map of String, Object.
     */
    private static Map<String, Object> convert(
        final Map<CharSequence, Object> params) {
        final Map<String, Object> map = new HashMap<>(params.size());
        for (final Map.Entry<CharSequence, Object> ent : params.entrySet()) {
            map.put(ent.getKey().toString(), ent.getValue());
        }
        return map;
    }

    /**
     * Pair of values.
     *
     * @since 0.1
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
