package org.takes.http;

/**
 * HTTP daemon back.
 *
 * <p></p>
 * @author Sebin George (sebing@gmail.com)
 * @version $Id$
 * @since 1.0
 */

public interface DaemonBack extends Back {
    /**
     * Start the damon thread.
     */
    void startDaemonThread();
}
