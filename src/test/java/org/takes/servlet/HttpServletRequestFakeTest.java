/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.servlet;

import java.util.Collections;
import java.util.NoSuchElementException;
import org.cactoos.list.ListOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasValues;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;
import org.takes.rq.RqWithHeaders;

/**
 * Test case for {@link  HttpServletRequestFake}.
 *
 * @since 1.15
 */
final class HttpServletRequestFakeTest {
    @Test
    void failsIfAHeaderIsNotFound() {
        final HttpServletRequestFake req = new HttpServletRequestFake(
            new RqWithHeaders(
                new RqFake(),
                "MyHeader: theValue",
                "MyOtherHeader: aValue"
            )
        );
        MatcherAssert.assertThat(
            Assertions.assertThrows(
                NoSuchElementException.class, () -> req.getHeader("foo")
            ),
            Matchers.hasProperty(
                "message",
                Matchers.is("Value of header foo not found")
            )
        );
    }

    @Test
    void containsAHeaderAndItsValue() {
        MatcherAssert.assertThat(
            "Can't get the headers",
            Collections.list(
                new HttpServletRequestFake(
                    new RqWithHeaders(
                        new RqFake(),
                        "TestHeader: someValue",
                        "SomeHeader: testValue"
                    )
                ).getHeaders("testheader")
            ),
            new HasValues<>(
                "someValue"
            )
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void containsAllHeadersNames() {
        MatcherAssert.assertThat(
            "Can't get the header names",
            Collections.list(
                new HttpServletRequestFake(
                    new RqWithHeaders(
                        new RqFake(),
                        "AnyHeader: anyValue",
                        "CrazyHeader: crazyValue"
                    )
                ).getHeaderNames()
            ),
            new IsIterableContainingInAnyOrder<>(
                new ListOf<>(
                    new IsEqual<>("host"),
                    new IsEqual<>("crazyheader"),
                    new IsEqual<>("anyheader")
                )
            )
        );
    }

    @Test
    void defaultMethodIsGet() {
        MatcherAssert.assertThat(
            "Can't get the request method",
            new HttpServletRequestFake(new RqFake()).getMethod(),
            new IsEqual<>(RqMethod.GET)
        );
    }

    @Test
    void defaultRequestId() {
        final HttpServletRequestFake req = new HttpServletRequestFake(
            new RqWithHeaders(
                new RqFake(),
                "MyHeader: theValue",
                "MyOtherHeader: aValue"
            )
        );
        MatcherAssert.assertThat(
            "Should be 1",
            req.getRequestId(),
            Matchers.equalTo("1")
        );
    }

    @Test
    void defaultProtocolRequestId() {
        final HttpServletRequestFake req = new HttpServletRequestFake(
            new RqWithHeaders(
                new RqFake(),
                "MyHeader: theValue",
                "MyOtherHeader: aValue"
            )
        );
        MatcherAssert.assertThat(
            "Should be empty",
            req.getProtocolRequestId(),
            Matchers.equalTo("")
        );
    }

    @Test
    void defaultServletConnection() {
        final HttpServletRequestFake req = new HttpServletRequestFake(
            new RqWithHeaders(
                new RqFake(),
                "MyHeader: theValue",
                "MyOtherHeader: aValue"
            )
        );
        MatcherAssert.assertThat(
            "Should be empty",
            req.getServletConnection(),
            Matchers.instanceOf(ServletConnectionFake.class)
        );
    }
}
