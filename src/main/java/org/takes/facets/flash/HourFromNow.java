/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.flash;

import java.util.concurrent.TimeUnit;
import org.takes.misc.Expires;

/**
 * Default expiration set to one hour from "now" (now is determined at
 * the time {@link #print()} is called).
 * @since 2.0
 */
final class HourFromNow implements Expires {

    @Override
    public String print() {
        return new Expires.Date(
            System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L)
        ).print();
    }
}
