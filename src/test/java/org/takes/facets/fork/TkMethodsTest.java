/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.facets.fork;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link TkMethods}.
 * @since 0.17
 */
final class TkMethodsTest {
    @Test
    void callsActOnProperMethods() throws Exception {
        final Take take = Mockito.mock(Take.class);
        final Request req = new RqFake(RqMethod.GET);
        new TkMethods(take, RqMethod.GET).act(req);
        Mockito.verify(take).act(req);
    }

    @Test
    void throwsExceptionOnActinOnUnproperMethod() {
        Assertions.assertThrows(
            HttpException.class,
            () -> new TkMethods(Mockito.mock(Take.class), RqMethod.POST).act(
                new RqFake(RqMethod.GET)
            )
        );
    }

    @Test
    void returnsMethodIsNotAllowedForUnsupportedMethods() throws
        Exception {
        new FtRemote(new TkMethods(new TkEmpty(), RqMethod.PUT)).exec(
            url -> new JdkRequest(url)
                .method(RqMethod.POST)
                .fetch().as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_BAD_METHOD)
        );
    }
}
