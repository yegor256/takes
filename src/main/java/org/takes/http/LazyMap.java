/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.util.Map;

/**
 * Map view that lazily parses the command-line arguments on first access.
 * @since 2.0
 */
final class LazyMap extends java.util.AbstractMap<String, String> {

    /**
     * Source arguments.
     */
    private final Iterable<String> args;

    /**
     * Cached parsed map.
     */
    private Map<String, String> cached;

    /**
     * Ctor.
     * @param source Source arguments
     */
    LazyMap(final Iterable<String> source) {
        this.args = source;
    }

    @Override
    public java.util.Set<Map.Entry<String, String>> entrySet() {
        return this.parsed().entrySet();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.parsed().containsKey(key);
    }

    @Override
    public String get(final Object key) {
        return this.parsed().get(key);
    }

    @Override
    public String getOrDefault(final Object key, final String def) {
        return this.parsed().getOrDefault(key, def);
    }

    private Map<String, String> parsed() {
        if (this.cached == null) {
            this.cached = Options.asMap(this.args);
        }
        return this.cached;
    }
}
