package org.takes.misc;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.collection.IsIterableWithSize;

/**
 * Tests for {@link VerboseIterator}.
 * @author marcus.sanchez (sanchez.marcus@gmail.com)
 * @version $Id$
 * @since 0.15.1
 */
public class VerboseIteratorTest {

    /**
     * VerboseIterator can return next value on a valid list.
     */
    @Test
    public void returnsNextValue() {
        MatcherAssert.assertThat(
            new VerboseIterable<String>(
                    Arrays.asList(
                            "Accept: text/plain",
                            "Accept-Charset: utf-8",
                            "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                            "Cache-Control: no-cache",
                            "From: user@example.com"
                            ),
                "Empty Error Message").iterator().next(),
                equalTo("Accept: text/plain")
            );
    }

    /**
     * VerboseIterator can inform has a next value on a valid list.
     */
    @Test
    public void informsHasNextValue() {
        MatcherAssert.assertThat(
                new VerboseIterable<String>(
                        Arrays.asList(
                                "Accept: text/plain",
                                "Accept-Charset: utf-8",
                                "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                                "From: user@example.com"
                        ),
                        "Empty Error Message"
                    )
                    .iterator().hasNext(), equalTo(true)
       );
    }

    /**
     * VerboseIterator next value throws exception on an empty list.
     */
    @Test(expected = RuntimeException.class)
    public void nextValueThrowsExceptionOnEmptyList() {
        new VerboseIterable<String>(
                Arrays.<String>asList(), 
                "Empty Error Message"
            )
            .iterator().next();
    }

    /**
     * VerboseIterator returns false in has next value on empty list.
     */
    @Test
    public void returnFalseInHasNextValueOnEmptyList() {
        MatcherAssert.assertThat(
                new VerboseIterable<String>(
                        Arrays.<String>asList(),
                        "Empty Error Message"
                        )
                        .iterator().hasNext(),
                equalTo(false)
        );
    }

    /**
     * @todo check if the remove value should be implemented, or leave it as it
     * is.<br/>
     * VerboseIterator can remove a value.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void removeValue() {
        final List<String> VALID = Arrays.asList(
                "Accept: text/plain",
                "Accept-Charset: utf-8",
                "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                "Cache-Control: no-cache",
                "From: user@example.com"
            );
        VerboseIterable<String> verboseIterable = new VerboseIterable<String>(
                VALID, "Empty Error Message"
            );
        verboseIterable.iterator().remove();
        MatcherAssert.assertThat(
                verboseIterable,
                IsIterableWithSize.<String> iterableWithSize(VALID.size() - 1)
        );
    }

}
