/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2025 Yegor Bugayenko
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
package org.takes.facets.auth.codecs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import org.takes.facets.auth.Identity;

/**
 * Compact codec.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.5
 */
@EqualsAndHashCode
public final class CcCompact implements Codec {

    @Override
    public byte[] encode(final Identity identity) {
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        try (DataOutputStream stream = new DataOutputStream(data)) {
            stream.writeUTF(identity.urn());
            for (final Map.Entry<String, String> ent
                : identity.properties().entrySet()) {
                stream.writeUTF(ent.getKey());
                stream.writeUTF(ent.getValue());
            }
        } catch (final IOException ex) {
            throw new IllegalArgumentException(
                "Failed to encode the identity",
                ex
            );
        }
        return data.toByteArray();
    }

    @Override
    public Identity decode(final byte[] bytes) {
        final Map<String, String> map = new HashMap<>(0);
        try (DataInputStream stream = new DataInputStream(
            new ByteArrayInputStream(bytes)
            )
        ) {
            final String urn = stream.readUTF();
            while (stream.available() > 0) {
                map.put(stream.readUTF(), stream.readUTF());
            }
            return new Identity.Simple(urn, map);
        } catch (final IOException ex) {
            throw new DecodingException(ex);
        }
    }

}
