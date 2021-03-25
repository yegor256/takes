/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2019 Yegor Bugayenko
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

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.cactoos.Text;
import org.cactoos.io.InputStreamOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Response;

/**
 * Test case for {@link RsGzip}.
 * @since 0.10
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class RsGzipTest {

    /**
     * RsGzip can build a compressed response.
     * @throws IOException If some problem inside
     */
    @Test
    void makesCompressedResponse() throws IOException {
        final String text = "some unicode text: \u20ac\n\t";
        final Response response = new RsGzip(new RsText(text));
        MatcherAssert.assertThat(
            new HeadPrint(response).asString(),
            Matchers.containsString("Content-Encoding: gzip")
        );
        MatcherAssert.assertThat(
            IOUtils.toString(
                new GZIPInputStream(response.body()),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo(text)
        );
    }

    /**
     * RsGzip can build a compressed PNG image.
     * @throws IOException If some problem inside
     */
    @Test
    void makesCompressedPngImage() throws IOException {
        final RenderedImage image = new BufferedImage(
            1, 1, BufferedImage.TYPE_INT_ARGB
        );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        final Text bodytxt = new BodyPrint(
            new RsGzip(
                new RsWithBody(baos.toByteArray())
            )
        );
        final BufferedImage reverse = ImageIO.read(
            new GZIPInputStream(
                new InputStreamOf(bodytxt)
            )
        );
        MatcherAssert.assertThat(reverse.getHeight(), Matchers.equalTo(1));
    }

    /**
     * RsGzip can report correct content length.
     * @throws IOException If some problem inside
     */
    @Test
    void reportsCorrectContentLength() throws IOException {
        final String text = "some text to encode";
        final Response response = new RsGzip(new RsText(text));
        MatcherAssert.assertThat(
            new HeadPrint(response).asString(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    new BodyPrint(response).length()
                )
            )
        );
    }

}
