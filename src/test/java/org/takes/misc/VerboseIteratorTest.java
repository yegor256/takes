package org.takes.misc;

import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;

/**
 * Tests for {@link VerboseIterator}.
 * 
 * @author marcus.sanchez (sanchez.marcus@gmail.com)
 * @version $Id$
 * @since 0.15.1
 */
public class VerboseIteratorTest {

    /**
     * VerboseIterator can return next value on a valid list
     */
    @Test
    public void returnsNextValue() {
        final List<String> VALID_LIST = Arrays.asList(
                "Accept: text/plain",
                "Accept-Charset: utf-8",
                "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                "Cache-Control: no-cache",
                "From: user@example.com");
        MatcherAssert.assertThat(
                new VerboseIterable<String>(VALID_LIST,  "Empty Error Message")
                        .iterator().next(),
                        equalTo("Accept: text/plain"));
    }

    /**
     * VerboseIterator can inform has a next value on a valid list
     */
    @Test
    public void informsHasNextValue() {
        final List<String> VALID_LIST = Arrays.asList(
                "Accept: text/plain",
                "Accept-Charset: utf-8",
                "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                "Cache-Control: no-cache",
                "From: user@example.com");
        MatcherAssert.assertThat(new VerboseIterable<String>(
                VALID_LIST,
                "Empty Error Message")
                .iterator().hasNext(), equalTo(true));
    }

    /**
     * VerboseIterator next value throws exception on an empty list
     */
    @Test(expected = RuntimeException.class)
    public void nextValueThrowsExceptionOnEmptyList() {
        final List<String> EMPTY_LIST = Arrays.asList();
        final String NO_NEXT_VALUE_ERROR = "Empty Error Message";
        new VerboseIterable<String>(
                        EMPTY_LIST, NO_NEXT_VALUE_ERROR).
                        iterator().next();
    }

    /**
     * VerboseIterator returns false in has next value on empty list
     */
    @Test
    public void returnFalseInHasNextValueOnEmptyList() {
        final List<String> EMPTY_LIST = Arrays.asList();
        final String NO_NEXT_VALUE_ERROR = "Empty Error Message";
        MatcherAssert.assertThat(
                new VerboseIterable<String>(
                    EMPTY_LIST,
                    NO_NEXT_VALUE_ERROR)
                    .iterator().hasNext(), equalTo(false));
    }

    /**
     * @todo check if the remove value should be implemented, or leave it as it
     *       is.<br/>
     *       VerboseIterator can remove a value
     */
    @Test(expected = UnsupportedOperationException.class)
    public void removeValue() {
        final List<String> VALID_LIST = Arrays.asList(
                "Accept: text/plain",
                "Accept-Charset: utf-8",
                "Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
                "Cache-Control: no-cache",
                "From: user@example.com");
        // Remove if verboseIterable.iterator().remove() is implemented
        VerboseIterable<String> verboseIterable = new VerboseIterable<String>(
                VALID_LIST, "Empty Error Message");
        verboseIterable.iterator().remove();
        MatcherAssert.assertThat(
                verboseIterable, IsIterableWithSize
                .<String> iterableWithSize(VALID_LIST.size() - 1));
    }

}
