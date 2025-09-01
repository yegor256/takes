/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.tk;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.takes.Take;
import org.takes.rq.RqGreedy;

/**
 * Take decorator that reads entire request body eagerly.
 *
 * <p>This {@link Take} decorator wraps another take and ensures that
 * the entire request body is read into memory before processing begins.
 * It uses {@link RqGreedy} to convert streaming request bodies into
 * fully materialized content that can be accessed multiple times.
 *
 * <p>The decorator is essential for takes that need to access request
 * body content multiple times or require random access to body data.
 * It addresses the limitation that HTTP request bodies are typically
 * streaming and can only be read once from their underlying input stream.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Ensure request body is fully read for processing
 * new TkGreedy(
 *     request -> {
 *         // Can safely read body multiple times
 *         String body1 = new RqPrint(request).printBody();
 *         String body2 = new RqPrint(request).printBody();
 *         return new RsText(body1.equals(body2) ? "Same" : "Different");
 *     }
 * );
 * 
 * // Useful for form processing that validates then processes
 * new TkGreedy(new TkFormProcessor());
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Form processing requiring validation and data extraction</li>
 *   <li>JSON/XML parsing with error handling and retry logic</li>
 *   <li>File upload processing with size validation</li>
 *   <li>Request logging and debugging scenarios</li>
 *   <li>Content transformation and filtering</li>
 *   <li>Multi-step request processing pipelines</li>
 *   <li>Request body caching and replay scenarios</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Entire request body is loaded into memory</li>
 *   <li>Memory usage increases with request body size</li>
 *   <li>Not suitable for large file uploads without size limits</li>
 *   <li>Eliminates streaming benefits for large payloads</li>
 * </ul>
 *
 * <p>The decorator preserves all request metadata including headers,
 * URI, and method while only modifying how the body content is accessed.
 * The wrapped take receives a request with identical semantics but with
 * the body fully materialized and reusable.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.10
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkGreedy extends TkWrap {

    /**
     * Ctor.
     * @param take Original take to wrap with greedy request reading
     */
    public TkGreedy(final Take take) {
        super(
            request -> take.act(new RqGreedy(request))
        );
    }

}
