package org.takes.tk;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

import java.io.IOException;

/**
 * Decorator TkRetry, which will not fail immediately on IOException, but will retry a few times.
 *
 * @author aschworer
 * @date 11-Dec-15
 */
public final class TkRetry implements Take {

    /**
     * how many times to retry, maximum
     */
    private final Integer retryCountMax;
    /**
     * initial delay between retries, in milliseconds
     */
    private final Integer delayBetweenRetries;

    /**
     * Original Take
     */
    private final Take originalTake;

    public TkRetry(Integer retryCountMax, Integer retryBetweenDelays, Take originalTake) {
        this.retryCountMax = retryCountMax;
        this.delayBetweenRetries = retryBetweenDelays;
        this.originalTake = originalTake;
    }

    /**
     * Added retrying logic
     *
     * @param req Request to process
     * @return
     * @throws IOException
     * @author aschworer
     */
    @Override
    public final Response act(final Request req) throws IOException {
        int attempts = 0;
        IOException e = new IOException();
        while (attempts++ < retryCountMax) {
            try {
                return this.originalTake.act(req);
            } catch (IOException ex) {
                e = ex;
                sleep();
            }
        }
        throw e;
    }

    private void sleep() {
        try {
            Thread.sleep(delayBetweenRetries);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

}
