/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import lombok.EqualsAndHashCode;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.xembly.Directive;
import org.xembly.Directives;
import org.xembly.SyntaxException;

/**
 * Chain of directives.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode
public final class XeDirectives implements XeSource {

    /**
     * Items.
     */
    private final IoChecked<Iterable<Directive>> directives;

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final String... dirs) {
        this(XeDirectives.transform(Arrays.asList(dirs)));
    }

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final Directive... dirs) {
        this(Arrays.asList(dirs));
    }

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final Iterable<Directive> dirs) {
        this(
            () -> dirs
        );
    }

    /**
     * Ctor.
     * @param dirs Directives
     */
    public XeDirectives(final Scalar<Iterable<Directive>> dirs) {
        this.directives = new IoChecked<>(dirs);
    }

    @Override
    public Iterable<Directive> toXembly() throws IOException {
        return this.directives.value();
    }

    /**
     * Transform strings to directives.
     * @param texts Texts
     * @return Directives
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private static Iterable<Directive> transform(final Iterable<String> texts) {
        final Collection<Directive> list = new LinkedList<>();
        for (final String text : texts) {
            try {
                for (final Directive dir : new Directives(text)) {
                    list.add(dir);
                }
            } catch (final SyntaxException ex) {
                throw new IllegalStateException(
                    "Failed to parse Xembly directives",
                    ex
                );
            }
        }
        return list;
    }

}
