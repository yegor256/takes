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
package org.takes.rq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link TempInputStream}.
 * @author Yohann Ferreira (yohann.ferreira@orange.fr)
 * @version $Id$
 * @since 0.21
 */
public final class TempInputStreamTest {

    /**
     * TempInputStream can delete the temporary cache file when closed.
     * @throws IOException if some problem occurs.
     */
    @Test
    public void deletesTempFile() throws IOException {
        final File file = File.createTempFile("tempfile", ".tmp");
        final BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write("takes is fun!\n");
        out.write("Temp file deletion test.\n");
        out.close();
        final InputStream body = new TempInputStream(
            new FileInputStream(file), file
        );
        MatcherAssert.assertThat(
            "File exists.",
            file.exists(),
            Matchers.equalTo(true)
        );
        body.close();
        MatcherAssert.assertThat(
            "File doesn't exist anymore.",
            file.exists(),
            Matchers.equalTo(false)
        );
    }
}
