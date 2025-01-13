/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
