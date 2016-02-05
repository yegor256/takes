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
package org.takes.rs;

import java.nio.charset.Charset;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Response;

/**
 * Response decorator, with content type.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class RsWithType extends RsWrap {

    /**
     * Type header.
     */
    private static final String HEADER = "Content-Type";

    /**
     * The name of the parameter allowing to define the character set.
     */
    private static final String CHARSET = "charset";

    /**
     * Content type text/html.
     */
    private static final String TYPE_HTML = "text/html";

    /**
     * Content type text/xml.
     */
    private static final String TYPE_XML = "text/xml";

    /**
     * Content type text/plain.
     */
    private static final String TYPE_TEXT = "text/plain";

    /**
     * Content type application/json.
     */
    private static final String TYPE_JSON = "application/json";

    /**
     * Ctor.
     * @param res Original response
     * @param type Content type
     */
    public RsWithType(final Response res, final CharSequence type) {
        super(RsWithType.make(res, type));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param type Content type
     * @param charset The character set to add in the content type header
     */
    public RsWithType(final Response res, final CharSequence type,
        final Charset charset) {
        super(RsWithType.make(res, type, charset));
    }

    /**
     * Ctor.
     * @param res Original response
     * @param type Content type
     * @return Response
     */
    private static Response make(final Response res, final CharSequence type) {
        return new RsWithHeader(
            new RsWithoutHeader(res, RsWithType.HEADER),
            RsWithType.HEADER, type
        );
    }

    /**
     * Factory allowing to create {@code Response} with the corresponding
     * content type and character set.
     * @param res Original response
     * @param type Content type
     * @param charset The character set to add in the content type header
     * @return Response
     */
    private static Response make(final Response res, final CharSequence type,
        final Charset charset) {
        return new RsWithHeader(
            new RsWithoutHeader(res, RsWithType.HEADER),
            RsWithType.HEADER,
            String.format("%s; %s=%s", type, RsWithType.CHARSET, charset.name())
        );
    }

    /**
     * Response decorator, with content type text/html.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Eléna Ihde-Simon (elena.ihde-simon@posteo.de)
     * @version $Id$
     * @since 0.30
     */
    public static final class HTML extends RsWrap {

        /**
         * Ctor.
         * @param res Original response
         */
        public HTML(final Response res) {
            super(RsWithType.make(res, RsWithType.TYPE_HTML));
        }

        /**
         * Ctor.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public HTML(final Response res, final Charset charset) {
            super(RsWithType.make(res, RsWithType.TYPE_HTML, charset));
        }

    }

    /**
     * Response decorator, with content type application/json.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Eléna Ihde-Simon (elena.ihde-simon@posteo.de)
     * @version $Id$
     * @since 0.30
     */
    public static final class JSON extends RsWrap {

        /**
         * Ctor.
         * @param res Original response
         */
        public JSON(final Response res) {
            super(RsWithType.make(res, RsWithType.TYPE_JSON));
        }

        /**
         * Ctor.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public JSON(final Response res, final Charset charset) {
            super(RsWithType.make(res, RsWithType.TYPE_JSON, charset));
        }

    }

    /**
     * Response decorator, with content type text/xml.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Eléna Ihde-Simon (elena.ihde-simon@posteo.de)
     * @version $Id$
     * @since 0.30
     */
    public static final class XML extends RsWrap {

        /**
         * Ctor.
         * @param res Original response
         */
        public XML(final Response res) {
            super(RsWithType.make(res, RsWithType.TYPE_XML));
        }

        /**
         * Ctor.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public XML(final Response res, final Charset charset) {
            super(RsWithType.make(res, RsWithType.TYPE_XML, charset));
        }

    }

    /**
     * Response decorator, with content type text/plain.
     *
     * <p>The class is immutable and thread-safe.
     *
     * @author Eléna Ihde-Simon (elena.ihde-simon@posteo.de)
     * @version $Id$
     * @since 0.30
     */
    public static final class Text extends RsWrap {

        /**
         * Ctor.
         * @param res Original response
         */
        public Text(final Response res) {
            super(RsWithType.make(res, RsWithType.TYPE_TEXT));
        }

        /**
         * Ctor.
         * @param res Original response
         * @param charset The character set to add in the content type header
         */
        public Text(final Response res, final Charset charset) {
            super(RsWithType.make(res, RsWithType.TYPE_TEXT, charset));
        }

    }
}
