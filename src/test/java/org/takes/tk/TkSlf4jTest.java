/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.tk;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkSlf4j}.
 * @since 0.11.2
 */
final class TkSlf4jTest {

    @Test
    void logsMessage() throws Exception {
        new TkSlf4j(new TkText("test")).act(new RqFake());
    }

    @Test
    void logsException() {
        final Take take = new TkSlf4j(new TkFailure(new IOException("")));
        Assertions.assertThrows(
            IOException.class,
            () -> take.act(new RqFake())
        );
    }

    @Test
    void logsRuntimeException() {
        final Take take = new TkSlf4j(new TkFailure(new RuntimeException("")));
        Assertions.assertThrows(
            RuntimeException.class,
            () -> take.act(new RqFake())
        );
    }

    @Test
    void logsEmptyMessage() {
        final Take take = new TkSlf4j(new TkEmpty());
        Assertions.assertDoesNotThrow(
            () -> take.act(new RqFake())
        );
    }
}
