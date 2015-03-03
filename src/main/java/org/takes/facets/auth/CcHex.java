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
package org.takes.facets.auth;

import java.io.IOException;
import lombok.EqualsAndHashCode;

/**
 * Hex codec.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.1
 */
@EqualsAndHashCode(of = "origin")
public final class CcHex implements Codec {

    /**
     * Backward mapping table.
     */
    private static final byte[] BACK = {
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0,
        0, 1, 2, 3, 4, 5, 6, 7,
        8, 9, 0, 0, 0, 0, 0, 0,
        0, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf, 0,
    };

    /**
     * Forward mapping table.
     */
    private static final byte[] FWD = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
    };

    /**
     * Original codec.
     */
    private final transient Codec origin;

    /**
     * Ctor.
     * @param codec Original codec
     */
    public CcHex(final Codec codec) {
        this.origin = codec;
    }

    @Override
    public byte[] encode(final Identity identity) throws IOException {
        final byte[] raw = this.origin.encode(identity);
        final byte[] out = new byte[raw.length << 1];
        for (int idx = 0; idx < raw.length; ++idx) {
            out[idx << 1] = CcHex.FWD[raw[idx] >> 4 & 0x0f];
            out[(idx << 1) + 1] = CcHex.FWD[raw[idx] & 0x0f];
        }
        return out;
    }

    @Override
    public Identity decode(final byte[] text) throws IOException {
        final byte[] out = new byte[text.length >> 1];
        for (int idx = 0; idx < out.length; ++idx) {
            out[idx] = (byte) ((CcHex.BACK[text[idx << 1]] << 4)
                + CcHex.BACK[text[(idx << 1) + 1]]);
        }
        return this.origin.decode(out);
    }

}
