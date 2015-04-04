package org.takes.misc;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

/**
 * Test case for {@link VerboseIterable}
 * @author Zarko Celebic
 */
public class VerboseIterableTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testIterator() throws Exception {
        List<String> testList = Arrays.asList("1", "2", "3", "4", "5");
        VerboseIterable<String> vIterable = new VerboseIterable<String>(testList, "Error Message");
        Iterator<String> iter = vIterable.iterator();

        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo("1"));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo("2"));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo("3"));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo("4"));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(true));
        MatcherAssert.assertThat(iter.next(), Matchers.equalTo("5"));
        MatcherAssert.assertThat(iter.hasNext(), Matchers.equalTo(false));

        expectedEx.expect(NoSuchElementException.class);
        expectedEx.expectMessage("Error Message");
        iter.next();
    }
}