/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.cactoos.Text;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Split;
import org.cactoos.text.UncheckedText;

/**
 * HTTP URI/HREF builder and parser with query parameter manipulation.
 *
 * <p>This class provides comprehensive functionality for building, parsing,
 * and manipulating HTTP URIs and HREFs. It supports automatic URL encoding/decoding,
 * query parameter management, path construction, and fragment handling.
 * The implementation handles malformed URIs by automatically encoding problematic
 * characters and provides a fluent interface for URI construction.
 *
 * <p>Key features:
 * <ul>
 * <li>Automatic URL encoding and decoding</li>
 * <li>Query parameter addition, removal, and retrieval</li>
 * <li>Path appending with proper encoding</li>
 * <li>Fragment support</li>
 * <li>Verbose error messages for missing parameters</li>
 * </ul>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.7
 */
@SuppressWarnings("PMD.GodClass")
public final class Href implements CharSequence {

    /**
     * Pattern matching trailing slash.
     */
    private static final Pattern TRAILING_SLASH = Pattern.compile("/$");

    /**
     * URI (without query and fragment parts).
     */
    private final org.cactoos.Scalar<URI> link;

    /**
     * Params.
     */
    private final org.cactoos.Scalar<SortedMap<String, List<String>>> params;

    /**
     * Fragment.
     */
    private final org.cactoos.Scalar<Opt<String>> frag;

    /**
     * Ctor.
     */
    public Href() {
        this("/");
    }

    /**
     * Ctor.
     * @param txt Text of the link
     */
    public Href(final CharSequence txt) {
        this(
            (org.cactoos.Scalar<URI>) () -> Href.createBare(
                Href.createUri(txt.toString())
            ),
            (org.cactoos.Scalar<SortedMap<String, List<String>>>) () -> Href.asMap(
                Href.createUri(txt.toString()).getRawQuery()
            ),
            (org.cactoos.Scalar<Opt<String>>) () -> Href.readFragment(
                Href.createUri(txt.toString())
            )
        );
    }

    /**
     * Ctor.
     * @param link The link
     * @param map Map of params
     * @param frgmnt Fragment part
     */
    private Href(final URI link,
        final SortedMap<String, List<String>> map,
        final Opt<String> frgmnt) {
        this(
            (org.cactoos.Scalar<URI>) () -> link,
            (org.cactoos.Scalar<SortedMap<String, List<String>>>) () -> map,
            (org.cactoos.Scalar<Opt<String>>) () -> frgmnt
        );
    }

    /**
     * Primary constructor with lazy holders.
     * @param uri URI scalar
     * @param map Params scalar
     * @param frg Fragment scalar
     */
    private Href(final org.cactoos.Scalar<URI> uri,
        final org.cactoos.Scalar<SortedMap<String, List<String>>> map,
        final org.cactoos.Scalar<Opt<String>> frg) {
        this.link = new org.cactoos.scalar.Sticky<>(uri);
        this.params = new org.cactoos.scalar.Sticky<>(map);
        this.frag = new org.cactoos.scalar.Sticky<>(frg);
    }

    @Override
    public int length() {
        final StringBuilder text = new StringBuilder(this.bare());
        this.appendParams(text);
        this.appendFragment(text);
        return text.length();
    }

    @Override
    public char charAt(final int index) {
        return this.toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(final int start, final int end) {
        return this.toString().subSequence(start, end);
    }

    @Override
    public String toString() {
        final StringBuilder text = new StringBuilder(this.bare());
        this.appendParams(text);
        this.appendFragment(text);
        return text.toString();
    }

    /**
     * Get path part of the HREF.
     * @return Path
     * @since 0.9
     */
    public String path() {
        return this.uri().getPath();
    }

    /**
     * Get URI without params.
     * @return Bare URI
     * @since 0.14
     */
    public String bare() {
        final StringBuilder text = new StringBuilder(this.uri().toString());
        if (this.uri().getPath().isEmpty()) {
            text.append('/');
        }
        return text.toString();
    }

    /**
     * Get query param.
     * @param key Param name
     * @return Values (could be empty)
     * @since 0.9
     */
    public Iterable<String> param(final Object key) {
        final List<String> values = this.params().getOrDefault(
            key.toString(),
            Collections.emptyList()
        );
        final Iterable<String> iter;
        if (values.isEmpty()) {
            iter = new VerboseIterable<>(
                Collections.emptyList(),
                new FormattedText(
                    "there are no URI params by name \"%s\" among %d others",
                    key, this.params().size()
                )
            );
        } else {
            iter = new VerboseIterable<>(
                values,
                new FormattedText(
                    "there are only %d URI params by name \"%s\"",
                    values.size(), key
                )
            );
        }
        return iter;
    }

    /**
     * Add this path to the URI.
     * @param suffix The suffix
     * @return New HREF
     */
    public Href path(final Object suffix) {
        return new Href(
            URI.create(
                new UncheckedText(
                    new FormattedText(
                        "%s/%s",
                        Href.TRAILING_SLASH.matcher(
                            this.uri().toString()
                        ).replaceAll(""),
                        Href.encode(suffix.toString())
                    )
                ).asString()
            ),
            this.params(),
            this.fragment()
        );
    }

    /**
     * Add this extra param.
     * @param key Key of the param
     * @param value The value
     * @return New HREF
     */
    public Href with(final Object key, final Object value) {
        final SortedMap<String, List<String>> map = new TreeMap<>(this.params());
        if (!map.containsKey(key.toString())) {
            map.put(key.toString(), new ArrayList<>(0));
        }
        map.get(key.toString()).add(value.toString());
        return new Href(this.uri(), map, this.fragment());
    }

    /**
     * Without this query param.
     * @param key Key of the param
     * @return New HREF
     */
    public Href without(final Object key) {
        final SortedMap<String, List<String>> map = new TreeMap<>(this.params());
        map.remove(key.toString());
        return new Href(this.uri(), map, this.fragment());
    }

    private void appendParams(final StringBuilder text) {
        if (!this.params().isEmpty()) {
            boolean first = true;
            for (final Map.Entry<String, List<String>> ent
                : this.params().entrySet()) {
                first = Href.appendParam(text, ent, first);
            }
        }
    }

    private static boolean appendParam(final StringBuilder text,
        final Map.Entry<String, List<String>> ent, final boolean first) {
        boolean result = first;
        for (final String value : ent.getValue()) {
            if (result) {
                text.append('?');
                result = false;
            } else {
                text.append('&');
            }
            text.append(Href.encode(ent.getKey()));
            if (!value.isEmpty()) {
                text.append('=').append(Href.encode(value));
            }
        }
        return result;
    }

    private void appendFragment(final StringBuilder text) {
        if (this.fragment().has()) {
            text.append('#');
            text.append(this.fragment().get());
        }
    }

    private static String encode(final String txt) {
        return URLEncoder.encode(txt, Charset.defaultCharset())
            .replace("+", "%20");
    }

    private static String decode(final String txt) {
        return URLDecoder.decode(txt, Charset.defaultCharset());
    }

    private static URI createUri(final String txt) {
        final StringBuilder value = new StringBuilder(txt);
        while (true) {
            try {
                return new URI(value.toString());
            } catch (final URISyntaxException ex) {
                final int index = ex.getIndex();
                if (index < 0 || index >= value.length()) {
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
                if (ex.getReason().contains("authority")) {
                    final StringBuilder message = new StringBuilder(64);
                    message
                        .append("Illegal URI: ")
                        .append(txt)
                        .append(". Parsing breaks on index ")
                        .append(index - (value.length() - txt.length()));
                    throw new IllegalArgumentException(
                        message.toString(),
                        ex
                    );
                }
                value.replace(
                    index,
                    index + 1,
                    Href.encode(value.substring(index, index + 1))
                );
            }
        }
    }

    private static SortedMap<String, List<String>> asMap(final String query) {
        final SortedMap<String, List<String>> params = new TreeMap<>();
        if (query != null) {
            for (final Text txt : new Split(query, "&")) {
                final String pair = new UncheckedText(txt).asString();
                final String[] parts = pair.split("=", 2);
                final String key = Href.decode(parts[0]);
                final String value;
                if (parts.length > 1) {
                    value = Href.decode(parts[1]);
                } else {
                    value = "";
                }
                if (!params.containsKey(key)) {
                    params.put(key, new ArrayList<>(0));
                }
                params.get(key).add(value);
            }
        }
        return params;
    }

    private static URI createBare(final URI link) {
        final URI uri;
        if (link.getRawQuery() == null && link.getRawFragment() == null) {
            uri = link;
        } else {
            final String href = link.toString();
            final int idx;
            if (link.getRawQuery() == null) {
                idx = href.indexOf('#');
            } else {
                idx = href.indexOf('?');
            }
            uri = URI.create(href.substring(0, idx));
        }
        return uri;
    }

    private static Opt<String> readFragment(final URI link) {
        final Opt<String> fragment;
        if (link.getRawFragment() == null) {
            fragment = new Opt.Empty<>();
        } else {
            fragment = new Opt.Single<>(link.getRawFragment());
        }
        return fragment;
    }

    private URI uri() {
        return new org.cactoos.scalar.Unchecked<>(this.link).value();
    }

    private SortedMap<String, List<String>> params() {
        return new org.cactoos.scalar.Unchecked<>(this.params).value();
    }

    private Opt<String> fragment() {
        return new org.cactoos.scalar.Unchecked<>(this.frag).value();
    }
}
