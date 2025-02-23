/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Response;

/**
 * Test case for {@link RsGzip}.
 * @since 0.10
 */
final class RsGzipTest {

    @Test
    void makesCompressedResponse() throws IOException {
        final String text = "some unicode text: \u20ac\n\t";
        final Response response = new RsGzip(new RsText(text));
        MatcherAssert.assertThat(
            new RsHeadPrint(response).asString(),
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

    @Test
    void makesCompressedPngImage() throws IOException {
        final int width = 42;
        final int height = 256;
        final RenderedImage image = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB
        );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        final BufferedImage reverse = ImageIO.read(
            new GZIPInputStream(
                new RsPrint(
                    new RsGzip(
                        new RsWithBody(baos.toByteArray())
                    )
                ).body()
            )
        );
        MatcherAssert.assertThat(reverse.getWidth(), Matchers.equalTo(width));
        MatcherAssert.assertThat(reverse.getHeight(), Matchers.equalTo(height));
    }

    @Test
    void reportsCorrectContentLength() throws IOException {
        final String text = "some text to encode";
        final Response response = new RsGzip(new RsText(text));
        MatcherAssert.assertThat(
            new RsHeadPrint(response).asString(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    new RsBodyPrint(response).asString().length()
                )
            )
        );
    }

}
