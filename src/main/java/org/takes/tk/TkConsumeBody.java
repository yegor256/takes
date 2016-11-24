package org.takes.tk;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqChunk;
import org.takes.rq.RqLengthAware;

import java.io.IOException;
import java.io.InputStream;

/**
 * A {@link Take} decorator that consumes the remaining body after
 * being processed by the inner {@link Take}.
 * <p>
 * <p>The class is immutable and thread-safe.
 *
 * @author Rui Castro (rui.castro@gmail.com)
 * @version $Id$
 * @since 0.43
 */
public class TkConsumeBody implements Take {

    /**
     * Original take.
     */
    private final Take origin;

    /**
     * Ctor.
     *
     * @param take Original
     */
    public TkConsumeBody(final Take take) {
        this.origin = take;
    }


    @Override
    public Response act(Request req) throws IOException {
        final Request reqSafe = new RqChunk(new RqLengthAware(req));
        final Response rsp = this.origin.act(reqSafe);
        // Consume the remaining body
        final InputStream body = reqSafe.body();
        for (int count = body.available(); count > 0;
             count = body.available()) {
            if (body.skip((long) count) < (long) count) {
                break;
            }
        }
        return rsp;
    }
}
