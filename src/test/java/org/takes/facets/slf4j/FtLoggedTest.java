/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Yegor Bugayenko
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

package org.takes.facets.slf4j;

import java.io.IOException;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.takes.http.Exit;
import org.takes.http.Front;

/**
 * Test case for {@link FtLogged}.
 * @author Dmitry Zaytsev (dmitry.zaytsev@gmail.com)
 * @version $Id$
 * @since 0.11
 */
public final class FtLoggedTest {
    /**
     * FtLogged can log message.
     * @throws IOException If some problem inside
     * @todo #90 we should change mocked Log4j appender to
     *  custom Log4jAppeneder that implemented as JUnit Rule.
     *  See details in CR comment here
     *  https://github.com/yegor256/takes/pull/89#discussion_r27354036
     */
    @Test
    public void logsMessage() throws IOException {
        final Appender appender = Mockito.mock(Appender.class);
        Mockito.when(appender.getName()).thenReturn("MOCK");
        Logger.getRootLogger().addAppender(appender);
        new FtLogged(
            new Front() {
                @Override
                @SuppressWarnings("PMD.UncommentedEmptyMethod")
                public void start(final Exit exit) throws IOException {
                }
            },
            LogWrap.Level.WARN
        ).start(
            new Exit() {
                @Override
                public boolean ready() {
                    return true;
                }
            }
        );
        Mockito.verify(appender)
            .doAppend(
                Matchers.argThat(
                    new ArgumentMatcher<LoggingEvent>() {
                        @Override
                        public boolean matches(final Object argument) {
                            return ((LoggingEvent) argument)
                                .getRenderedMessage()
                                .contains("start");
                        }
                    }
                )
            );
    }
}
