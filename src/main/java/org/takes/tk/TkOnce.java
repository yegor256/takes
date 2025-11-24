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
 * Take decorator that ensures the request body can only be read once.
 *
 * <p>This {@link Take} decorator wraps another take and ensures that
 * the request body is fully consumed and materialized before processing.
 * It uses {@link RqGreedy} to read the entire request body into memory,
 * making it available for single-pass processing while preventing
 * multiple reads of the underlying input stream.
 *
 * <p>This decorator is useful for takes that process streaming request
 * bodies in a single pass, ensuring that the entire content is available
 * immediately and preventing accidental re-reads of already consumed streams.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Ensure request is read only once for processing
 * new TkOnce(
 *     request -> {
 *         String body = new RqPrint(request).printBody();
 *         // Process body once
 *         return new RsText("Processed: " + body.length() + " bytes");
 *     }
 * );
 *
 * // Wrap file upload handler
 * new TkOnce(new TkFileUpload());
 *
 * // Single-pass JSON processing
 * new TkOnce(new TkJsonProcessor());
 * }</pre>
 *
 * <p>Common use cases include:
 * <ul>
 *   <li>Stream processing that must read content sequentially</li>
 *   <li>Large file uploads with single-pass validation</li>
 *   <li>Request body transformation and filtering</li>
 *   <li>Security scanning of request content</li>
 *   <li>Audit logging of complete requests</li>
 *   <li>Content type detection and routing</li>
 * </ul>
 *
 * <p>The decorator ensures that the request body is fully available
 * before the wrapped take processes it, eliminating issues with
 * partially read streams or connection timeouts during processing.
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Entire request body is loaded into memory</li>
 *   <li>Not suitable for very large uploads without memory limits</li>
 *   <li>Provides predictable memory usage patterns</li>
 *   <li>Eliminates network I/O during request processing</li>
 * </ul>
 *
 * <p>The class is immutable and thread-safe.
 *
 * @since 0.26
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TkOnce extends TkWrap {

    /**
     * Ctor.
     * @param take Original take
     */
    public TkOnce(final Take take) {
        super(
            request -> take.act(new RqGreedy(request))
        );
    }

}
