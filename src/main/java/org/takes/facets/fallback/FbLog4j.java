package org.takes.facets.fallback;

import lombok.EqualsAndHashCode;
import org.apache.log4j.Logger;
import org.takes.Response;
import org.takes.misc.Opt;
import org.takes.rq.RqHref;
import org.takes.rq.RqMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Igor Piddubnyi (igor.piddubnyi@gmail.com).
 */
@EqualsAndHashCode(callSuper = true)
public final class FbLog4j extends FbWrap{

    private static final Logger LOGGER = Logger.getLogger(FbLog4j.class);


    /**
     * Ctor.
     */
    public FbLog4j() {
        super(new Fallback() {
            @Override
            public Opt<Response> route(RqFallback req) throws IOException {
                FbLog4j.log(req);
                return new Opt.Empty<Response>();
            }
        });
    }

    private static void log(RqFallback req) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Throwable error = req.throwable();
        final PrintWriter writer = new PrintWriter(baos);
        error.printStackTrace(writer);
        writer.close();
        FbLog4j.LOGGER.error(String.format("%s %s failed with %s: %s",
                new RqMethod.Base(req).method(),
                new RqHref.Base(req).href(),
                req.code(),
                baos.toString("UTF-8"))
        );
    }
}
