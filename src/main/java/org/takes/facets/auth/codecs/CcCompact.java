/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
