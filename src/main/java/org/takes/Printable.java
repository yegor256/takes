/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Printable HTTP message.
 *
 * <p>An object implementing this interface can print a complete HTTP message,
 * its head, or its body into a string or an output stream.
 *
 * @since 2.0
 */
public interface Printable {

    /**
     * Print the complete HTTP message into a string.
     * @return Complete HTTP message
     * @throws IOException If something goes wrong
     */
    String print() throws IOException;

    /**
     * Print the complete HTTP message into an output stream.
     * @param output Output stream
     * @throws IOException If something goes wrong
     */
    void print(OutputStream output) throws IOException;

    /**
     * Print the HTTP message head into a string.
     * @return HTTP message head
     * @throws IOException If something goes wrong
     */
    String printHead() throws IOException;

    /**
     * Print the HTTP message head into an output stream.
     * @param output Output stream
     * @throws IOException If something goes wrong
     */
    void printHead(OutputStream output) throws IOException;

    /**
     * Print the HTTP message body into a string.
     * @return HTTP message body
     * @throws IOException If something goes wrong
     */
    String printBody() throws IOException;

    /**
     * Print the HTTP message body into an output stream.
     * @param output Output stream
     * @throws IOException If something goes wrong
     */
    void printBody(OutputStream output) throws IOException;
}
