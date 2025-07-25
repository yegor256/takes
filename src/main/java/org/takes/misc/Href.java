/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.misc;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.cactoos.text.FormattedText;

/**
 * HTTP URI/HREF.
 *
 * <p>The class is immutable and thread-safe.
 * @since 0.7
 */
@SuppressWarnings(
    {
        "PMD.TooManyMethods",
        "PMD.OnlyOneConstructorShouldDoInitialization",
        "PMD.GodClass"
    }
)
public final class Href implements CharSequence {

    /**
     * Pattern matching trailing slash.
     */
    private static final Pattern TRAILING_SLASH = Pattern.compile("/$");

    /**
     * URI (without query and fragment parts).
     */
    private final URI uri;

    /**
     * Params.
     */
    private final SortedMap<String, List<String>> params;

    /**
     * Fragment.
     */
    private final Opt<String> fragment;

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
        this(Href.createUri(txt.toString()));
    }

    /**
     * Ctor.
     * @param link The link
     */
    private Href(final URI link) {
        this(Href.createBare(link), Href.asMap(link.getRawQuery()),
            Href.readFragment(link));
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
        this.uri = link;
        this.params = map;
        this.fragment = frgmnt;
    }

    @Override
    public int length() {
        return this.toString().length();
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
        return this.uri.getPath();
    }

    /**
     * Get URI without params.
     * @return Bare URI
     * @since 0.14
     */
    public String bare() {
        final StringBuilder text = new StringBuilder(this.uri.toString());
        if (this.uri.getPath().isEmpty()) {
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
        final List<String> values = this.params.getOrDefault(
            key.toString(),
            Collections.emptyList()
        );
        final Iterable<String> iter;
        if (values.isEmpty()) {
            iter = new VerboseIterable<>(
                Collections.emptyList(),
                new FormattedText(
                    "there are no URI params by name \"%s\" among %d others",
                    key, this.params.size()
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
                new StringBuilder(
                    Href.TRAILING_SLASH.matcher(this.uri.toString())
                        .replaceAll("")
                )
                .append('/')
                .append(Href.encode(suffix.toString())).toString()
            ),
            this.params,
            this.fragment
        );
    }

    /**
     * Add this extra param.
     * @param key Key of the param
     * @param value The value
     * @return New HREF
     */
    public Href with(final Object key, final Object value) {
        final SortedMap<String, List<String>> map = new TreeMap<>(this.params);
        if (!map.containsKey(key.toString())) {
            map.put(key.toString(), new LinkedList<>());
        }
        map.get(key.toString()).add(value.toString());
        return new Href(this.uri, map, this.fragment);
    }

    /**
     * Without this query param.
     * @param key Key of the param
     * @return New HREF
     */
    public Href without(final Object key) {
        final SortedMap<String, List<String>> map = new TreeMap<>(this.params);
        map.remove(key.toString());
        return new Href(this.uri, map, this.fragment);
    }

    /**
     * Append parameters to StringBuilder.
     * @param text StringBuilder to append to
     */
    private void appendParams(final StringBuilder text) {
        if (!this.params.isEmpty()) {
            boolean first = true;
            for (final Map.Entry<String, List<String>> ent
                : this.params.entrySet()) {
                first = Href.appendParam(text, ent, first);
            }
        }
    }

    /**
     * Append single parameter to StringBuilder.
     * @param text StringBuilder to append to
     * @param ent Parameter entry
     * @param first Whether this is the first parameter
     * @return Whether next parameter will be first
     */
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

    /**
     * Append fragment to StringBuilder.
     * @param text StringBuilder to append to
     */
    private void appendFragment(final StringBuilder text) {
        if (this.fragment.has()) {
            text.append('#');
            text.append(this.fragment.get());
        }
    }

    /**
     * Encode into URL.
     * @param txt Text
     * @return Encoded
     */
    private static String encode(final String txt) {
        try {
            return URLEncoder.encode(
                txt, Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(
                String.format("Failed to encode '%s'", txt),
                ex
            );
        }
    }

    /**
     * Decode from URL.
     * @param txt Text
     * @return Decoded
     */
    private static String decode(final String txt) {
        try {
            return URLDecoder.decode(
                txt, Charset.defaultCharset().name()
            );
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(
                String.format("Failed to decode '%s'", txt),
                ex
            );
        }
    }

    /**
     * Parses the specified content to create the corresponding {@code URI}
     * instance. In case of an {@code URISyntaxException}, it will automatically
     * encode the character that causes the issue then it will try again
     * if it is possible otherwise an {@code IllegalArgumentException} will
     * be thrown.
     * @param txt The content to parse
     * @return The {@code URI} corresponding to the specified content.
     * @throws IllegalArgumentException in case the content could not be parsed
     * @throws IllegalStateException in case an invalid character could not be
     *  encoded properly.
     */
    private static URI createUri(final String txt) {
        final StringBuilder value = new StringBuilder(txt);
        while (true) {
            try {
                return new URI(value.toString());
            } catch (final URISyntaxException ex) {
                final int index = ex.getIndex();
                if (index < 0 || index >= value.length()) {
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                } else if (ex.getReason().contains("authority")) {
                    final StringBuilder errMessage = new StringBuilder();
                    errMessage
                        .append("Illegal URI: ")
                        .append(txt)
                        .append(". Parsing breaks on index ")
                        .append((index - (value.length() - txt.length())));
                    throw new IllegalArgumentException(
                        errMessage.toString(),
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

    /**
     * Convert the provided query into a Map.
     * @param query The query to parse.
     * @return A map containing all the query arguments and their values.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static SortedMap<String, List<String>> asMap(final String query) {
        final SortedMap<String, List<String>> params = new TreeMap<>();
        if (query != null) {
            for (final String pair : query.split("&")) {
                final String[] parts = pair.split("=", 2);
                final String key = Href.decode(parts[0]);
                final String value;
                if (parts.length > 1) {
                    value = Href.decode(parts[1]);
                } else {
                    value = "";
                }
                if (!params.containsKey(key)) {
                    params.put(key, new LinkedList<>());
                }
                params.get(key).add(value);
            }
        }
        return params;
    }

    /**
     * Remove query and fragment parts from the provided URI and
     *   return the resulting URI.
     * @param link The link from which parts need to be removed.
     * @return The URI corresponding to the same provided URI but without
     *  query and fragment parts.
     */
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

    /**
     * Read fragment part from the given URI.
     * @param link The link from which the fragment needs to be returned.
     * @return Opt with fragment or empty if there is no fragment.
     */
    private static Opt<String> readFragment(final URI link) {
        final Opt<String> fragment;
        if (link.getRawFragment() == null) {
            fragment = new Opt.Empty<>();
        } else {
            fragment = new Opt.Single<>(link.getRawFragment());
        }
        return fragment;
    }
}
