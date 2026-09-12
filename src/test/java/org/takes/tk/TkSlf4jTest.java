/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.tk;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkSlf4j}.
 * @since 0.11.2
 */
final class TkSlf4jTest {

    @Test
    void logsMessage() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> new TkSlf4j(new TkText("test")).act(new RqFake())
        );
    }

    @Test
    void logsException() {
        Assertions.assertThrows(
            IOException.class,
            () -> new TkSlf4j(new TkFailure(new IOException(""))).act(new RqFake())
        );
    }

    @Test
    void logsRuntimeException() {
        Assertions.assertThrows(
            RuntimeException.class,
            () -> new TkSlf4j(new TkFailure(new RuntimeException(""))).act(new RqFake())
        );
    }

    @Test
    void logsEmptyMessage() {
        Assertions.assertDoesNotThrow(
            () -> new TkSlf4j(new TkEmpty()).act(new RqFake())
        );
    }
}
