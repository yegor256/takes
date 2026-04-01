/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
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
@SuppressWarnings("PMD.UnnecessaryLocalRule")
final class RsGzipTest {

    @Test
    void addsGzipContentEncodingHeader() throws IOException {
        MatcherAssert.assertThat(
            "Gzip response must contain Content-Encoding header",
            new RsHeadPrint(
                new RsGzip(new RsText("some unicode text: \u20ac\n\t"))
            ).asString(),
            Matchers.containsString("Content-Encoding: gzip")
        );
    }

    @Test
    void decompressesToOriginalText() throws IOException {
        final String text = "some unicode text: \u20ac\n\t";
        MatcherAssert.assertThat(
            "Decompressed gzip content must match original text",
            IOUtils.toString(
                new GZIPInputStream(new RsGzip(new RsText(text)).body()),
                StandardCharsets.UTF_8
            ),
            Matchers.equalTo(text)
        );
    }

    @Test
    void compressesPngImageWidth() throws IOException {
        final int width = 42;
        MatcherAssert.assertThat(
            "Decompressed image width must match original",
            RsGzipTest.decompressImage(width, 256).getWidth(),
            Matchers.equalTo(width)
        );
    }

    @Test
    void compressesPngImageHeight() throws IOException {
        final int height = 256;
        MatcherAssert.assertThat(
            "Decompressed image height must match original",
            RsGzipTest.decompressImage(42, height).getHeight(),
            Matchers.equalTo(height)
        );
    }

    @Test
    void reportsCorrectContentLength() throws IOException {
        final String text = "some text to encode";
        final Response response = new RsGzip(new RsText(text));
        MatcherAssert.assertThat(
            "Gzip response must report correct compressed content length",
            new RsHeadPrint(response).asString(),
            Matchers.containsString(
                String.format(
                    "Content-Length: %d",
                    new RsBodyPrint(response).asString().length()
                )
            )
        );
    }

    private static BufferedImage decompressImage(
        final int width, final int height
    ) throws IOException {
        final RenderedImage image = new BufferedImage(
            width, height, BufferedImage.TYPE_INT_ARGB
        );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return ImageIO.read(
            new GZIPInputStream(
                new RsPrint(
                    new RsGzip(
                        new RsWithBody(baos.toByteArray())
                    )
                ).body()
            )
        );
    }

}
