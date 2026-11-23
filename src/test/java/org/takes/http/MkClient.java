/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Raw HTTP client over one socket, for testing connection reuse.
 *
 * <p>HTTP client libraries hide whether a connection is reused, closed
 * or pipelined. This client writes request lines verbatim on a single
 * socket and reads the raw bytes back, never waiting longer than five
 * seconds for a read. The test that opens the socket closes it.</p>
 *
 * @since 1.20
 */
final class MkClient {

    /**
     * Socket to the server.
     */
    private final Socket socket;

    /**
     * Ctor.
     *
     * @param skt Socket to the server
     */
    MkClient(final Socket skt) {
        this.socket = skt;
    }

    /**
     * Send lines joined with CRLF.
     *
     * @param lines Lines to send
     * @throws IOException If fails
     */
    void send(final String... lines) throws IOException {
        this.socket.getOutputStream().write(
            String.join(String.valueOf((char) 13) + (char) 10, lines)
                .getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Abort the connection with a TCP reset.
     *
     * @throws IOException If fails
     */
    void reset() throws IOException {
        this.socket.setSoLinger(true, 0);
        this.socket.close();
    }

    /**
     * Stop sending, while still reading what the server answers.
     *
     * @throws IOException If fails
     */
    void leave() throws IOException {
        this.socket.shutdownOutput();
    }

    /**
     * Read until the text arrives or the server closes the connection.
     *
     * @param text Text to wait for
     * @return Everything read
     * @throws IOException If fails
     */
    String until(final String text) throws IOException {
        this.socket.setSoTimeout(5000);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        int count = 0;
        while (
            count >= 0
                && !output.toString(StandardCharsets.UTF_8).contains(text)
        ) {
            count = this.socket.getInputStream().read(buffer);
            if (count > 0) {
                output.write(buffer, 0, count);
            }
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    /**
     * Read until the server closes the connection.
     *
     * @return Everything read
     * @throws IOException If fails
     */
    String rest() throws IOException {
        this.socket.setSoTimeout(5000);
        return new String(
            this.socket.getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );
    }
}
