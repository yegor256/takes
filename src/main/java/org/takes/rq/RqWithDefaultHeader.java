/**
 * Copyright (c) 2009-2015, netbout.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are PROHIBITED without prior written permission from
 * the author. This product may NOT be used anywhere and on any computer
 * except the server platform of netbout Inc. located at www.netbout.com.
 * Federal copyright law prohibits unauthorized reproduction by any means
 * and imposes fines up to $25,000 for violation. If you received
 * this code accidentally and without intent to use it, please report this
 * incident to the author by email.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.takes.rq;

import java.io.IOException;
import org.takes.Request;

/**
 * Request with default header.
 * @author Andrey Eliseev (aeg.exper0@gmail.com)
 * @version $Id$
 */
public final class RqWithDefaultHeader extends RqWrap {

    /**
     * Ctor.
     * @param req Original request
     * @param hdr Header name
     * @param val Header value
     * @throws IOException in case of request errors
     */
    public RqWithDefaultHeader(final Request req,
        final String hdr,
        final String val) throws IOException {
        super(RqWithDefaultHeader.buildRequest(req, hdr, val));
    }

    /**
     * Builds the request with the default header if it is not already present.
     * @param req Original request.
     * @param hdr Header name.
     * @param val Header value.
     * @return The new request.
     * @throws IOException in case of request errors
     */
    private static Request buildRequest(final Request req,
        final String hdr, final String val) throws IOException {
        final Request request;
        if (new RqHeaders.Base(req).header(hdr).iterator().hasNext()) {
            request = req;
        } else {
            request = new RqWithHeader(req, hdr, val);
        }
        return request;
    }

}
