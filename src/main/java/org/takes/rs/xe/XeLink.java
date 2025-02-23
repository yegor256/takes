/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.rs.xe;

import lombok.EqualsAndHashCode;
import org.xembly.Directives;

/**
 * Xembly source to create an Atom LINK element.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.1
 */
@EqualsAndHashCode(callSuper = true)
public final class XeLink extends XeWrap {

    /**
     * Ctor.
     * @param related Related
     * @param link HREF
     */
    public XeLink(final CharSequence related, final CharSequence link) {
        this(related, link, "text/xml");
    }

    /**
     * Ctor.
     * @param rel Related
     * @param href HREF
     * @param type Content type
     */
    public XeLink(final CharSequence rel, final CharSequence href,
        final CharSequence type) {
        super(
            () -> new Directives()
                .addIf("links")
                .add("link")
                .attr("rel", rel.toString())
                .attr("href", href.toString())
                .attr("type", type.toString())
                .up()
                .up()
        );
    }

}
