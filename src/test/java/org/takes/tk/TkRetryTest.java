package org.takes.tk;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import org.takes.Request;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rs.RsPrint;

import java.io.IOException;
import java.net.URI;

/**
 * Test for TkRetry
 *
 * @author aschworer
 * @date 15-Dec-15
 */
public final class TkRetryTest {

    /**
     * TkRetry works
     *
     * @throws Exception
     */
    @Test
    public void justWorks() throws Exception {
        new FtRemote(new TkFixed("hello, world!")).exec(
                new FtRemote.Script() {
                    @Override
                    public void exec(final URI home) throws IOException {
                        MatcherAssert.assertThat(
                                new RsPrint(
                                        new TkRetry(3, 4000, new TkText("test")).act(new RqFake())
                                ).print(),
                                Matchers.containsString("test")
                        );
                    }
                }
        );
    }

    /**
     * TkRetry retries when failed with IOException
     *
     * @throws Exception
     */
    @Test(expected = IOException.class)
    public void retries() throws Exception {
        final int numberOfTimesToTry = 3;
        final int delay = 1000;

        final Take take = Mockito.mock(Take.class);
        final Request req = new RqFake(RqMethod.GET);
        Mockito.when(take.act(req)).thenThrow(new IOException());

        final long minTimeToFail = numberOfTimesToTry * delay;
        final long startTime = System.currentTimeMillis();
        try {
            new TkRetry(numberOfTimesToTry, delay, take).act(req);
        } catch (IOException e) {
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            MatcherAssert.assertThat(minTimeToFail, Matchers.lessThanOrEqualTo(elapsedTime));
            throw e;
        }
    }


}
