/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.auth;

import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rs.xe.XeWrap;
import org.xembly.Directives;

/**
 * Xembly source to show authenticated identity.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.4
 */
@EqualsAndHashCode(callSuper = true)
public final class XeIdentity extends XeWrap {

    /**
     * Ctor.
     * @param req Request
     */
    public XeIdentity(final Request req) {
        super(
            () -> {
                final Identity identity = new RqAuth(req).identity();
                final Directives dirs = new Directives();
                if (!identity.equals(Identity.ANONYMOUS)) {
                    dirs.add("identity")
                        .add("urn").set(identity.urn()).up();
                    for (final Map.Entry<String, String> prop
                        : identity.properties().entrySet()) {
                        dirs.add(prop.getKey()).set(prop.getValue()).up();
                    }
                }
                return dirs;
            }
        );
    }

}
