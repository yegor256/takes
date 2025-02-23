/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import java.util.ResourceBundle;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rs.RsWithHeader;

/**
 * Take that adds an HTTP header to each response with a version
 * of Take framework.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkVersioned extends TkWrap {

    /**
     * Version of the library.
     */
    private static final String VERSION = TkVersioned.make();

    /**
     * Ctor.
     * @param take Original take
     */
    public TkVersioned(final Take take) {
        this(take, "X-Takes-Version");
    }

    /**
     * Ctor.
     * @param take Original take
     * @param header Header to add
     */
    public TkVersioned(final Take take, final String header) {
        super(
            req -> new RsWithHeader(
                take.act(req), header, TkVersioned.VERSION
            )
        );
    }

    /**
     * Make a version.
     * @return Version
     */
    private static String make() {
        final ResourceBundle res =
            ResourceBundle.getBundle("org.takes.version");
        return String.format(
            "%s %s %s",
            res.getString("version"),
            res.getString("revision"),
            res.getString("date")
        );
    }

}
