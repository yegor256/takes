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
package org.takes.facets.fork;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;

/**
 * Fork by hit-refresh header.
 *
 * <p>The class is immutable and thread-safe.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.9
 * @see TkFork
 */
@SuppressWarnings("PMD.DoNotUseThreads")
@EqualsAndHashCode(of = { "dir", "exec", "last", "target" })
public final class FkHitRefresh implements Fork {

    /**
     * Directory to watch.
     */
    private final transient File dir;

    /**
     * Command to execute.
     */
    private final transient Runnable exec;

    /**
     * Target.
     */
    private final transient Target<Request> target;

    /**
     * File touched on every exec run.
     */
    private final transient File last;

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param take Take
     * @throws IOException If fails
     */
    public FkHitRefresh(final File file, final String cmd, final Take take)
        throws IOException {
        this(
            file, cmd,
            new Target<Request>() {
                @Override
                public Response act(final Request req) throws IOException {
                    return take.act(req);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param take Take
     * @throws IOException If fails
     */
    public FkHitRefresh(final File file, final Runnable cmd, final Take take)
        throws IOException {
        this(
            file, cmd,
            new Target<Request>() {
                @Override
                public Response act(final Request req) throws IOException {
                    return take.act(req);
                }
            }
        );
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param tgt Target
     * @throws IOException If fails
     */
    public FkHitRefresh(final File file, final String cmd,
        final Target<Request> tgt) throws IOException {
        this(file, Arrays.asList(cmd.split(" ")), tgt);
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param tgt Target
     * @throws IOException If fails
     */
    public FkHitRefresh(final File file, final List<String> cmd,
        final Target<Request> tgt) throws IOException {
        this(
            file,
            new Runnable() {
                @Override
                public void run() {
                    try {
                        new ProcessBuilder().command(cmd).start();
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            },
            tgt
        );
    }

    /**
     * Ctor.
     * @param file Directory to watch
     * @param cmd Command to execute
     * @param tgt Target
     * @throws IOException If fails
     */
    public FkHitRefresh(final File file, final Runnable cmd,
        final Target<Request> tgt) throws IOException {
        this.dir = file;
        this.exec = cmd;
        this.target = tgt;
        this.last = File.createTempFile("take", ".txt");
        this.last.deleteOnExit();
        this.touch();
    }

    @Override
    public Iterator<Response> route(final Request req) throws IOException {
        final Iterator<String> header =
            new RqHeaders(req).header("X-Take-HitRefresh").iterator();
        final Collection<Response> response = new ArrayList<Response>(1);
        if (header.hasNext()) {
            if (this.expired()) {
                this.exec.run();
                this.touch();
            }
            response.add(this.target.act(req));
        }
        return response.iterator();
    }

    /**
     * Expired?
     * @return TRUE if expired
     */
    private boolean expired() {
        final long recent = this.last.lastModified();
        boolean expired = false;
        final File[] files = this.dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.lastModified() > recent) {
                    expired = true;
                    break;
                }
            }
        }
        return expired;
    }

    /**
     * Touch the file.
     * @throws IOException If fails
     */
    private void touch() throws IOException {
        final OutputStream out = new FileOutputStream(this.last);
        try {
            out.write('+');
        } finally {
            out.close();
        }
    }

}
