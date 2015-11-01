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
package org.takes.tk;

import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

/**
 * Proxy take.
 *
 * <p>This take may transform the request before passing it
 * to the original take and/or transform the response received
 * from the original take.
 *
 * <p>If the request transformer is not provided, the original
 * request is passed to the original take.
 *
 * <p>If the response transformer is not provided, the response
 * received from the original take is returned.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Dragan Bozanovic (bozanovicdr@gmail.com)
 * @version $Id$
 * @since 0.25
 */
@ToString
@EqualsAndHashCode(of = { "origin", "rqtransformer", "rstransformer" })
public final class TkProxy implements Take {

    /**
     * Original take.
     */
    private final transient Take origin;

    /**
     * Request transformer.
     */
    private final transient RqTransformer rqtransformer;

    /**
     * Response transformer.
     */
    private final transient RsTransformer rstransformer;

    /**
     * Ctor.
     * @param take Original take
     * @param rqtransformr Request transformer
     * @param rstransformr Response transformer
     */
    public TkProxy(final Take take, final RqTransformer rqtransformr,
            final RsTransformer rstransformr) {
        this.origin = take;
        this.rqtransformer = rqtransformr;
        this.rstransformer = rstransformr;
    }

    /**
     * Ctor.
     * @param take Original take
     * @param rqtransformr Request transformer
     */
    public TkProxy(final Take take, final RqTransformer rqtransformr) {
        this(take, rqtransformr, new RsTransformer() {
            @Override
            public Response transform(final Response response) {
                return response;
            }
        });
    }

    /**
     * Ctor.
     * @param take Original take
     * @param rstransformr Response transformer
     */
    public TkProxy(final Take take, final RsTransformer rstransformr) {
        this(take, new RqTransformer() {
            @Override
            public Request transform(final Request request) {
                return request;
            }
        }, rstransformr);
    }

    @Override
    public Response act(final Request req) throws IOException {
        return this.rstransformer.transform(
            this.origin.act(
                this.rqtransformer.transform(req)
            )
        );
    }

    /**
     * Request transformer.
     */
    public interface RqTransformer {

        /**
         * Transforms the original request.
         * @param request Original request
         * @return The transformed request
         */
        Request transform(Request request);
    }

    /**
     * Response transformer.
     */
    public interface RsTransformer {

        /**
         * Transforms the original response.
         * @param response Original response
         * @return Transformed response
         */
        Response transform(Response response);
    }
}
