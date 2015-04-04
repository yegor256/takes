package org.takes.misc;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Test case for {@link VerboseIterator}
 * @author Zarko Celebic
 */
public final class VerboseIteratorTest{

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testHasNext() throws Exception {
        List<String> testList = Arrays.asList("1", "2", "3", "4", "5");
        VerboseIterator<String> vi = new VerboseIterator<String>(testList.iterator(), "Error message");
        MatcherAssert.assertThat(vi.hasNext(), Matchers.equalTo(true));
        vi.next();
        MatcherAssert.assertThat(vi.hasNext(), Matchers.equalTo(true));
        vi.next();
        MatcherAssert.assertThat(vi.hasNext(), Matchers.equalTo(true));
        vi.next();
        MatcherAssert.assertThat(vi.hasNext(), Matchers.equalTo(true));
        vi.next();
        MatcherAssert.assertThat(vi.hasNext(), Matchers.equalTo(true));
        vi.next();
        MatcherAssert.assertThat(vi.hasNext(), Matchers.equalTo(false));

    }

    @Test
    public void testNext() throws Exception {
        List<String> testList = Arrays.asList("1", "2", "3", "4", "5");
        VerboseIterator<String> vi = new VerboseIterator<String>(testList.iterator(), "Error message");
        MatcherAssert.assertThat(vi.next(), Matchers.equalTo("1"));
        MatcherAssert.assertThat(vi.next(), Matchers.equalTo("2"));
        MatcherAssert.assertThat(vi.next(), Matchers.equalTo("3"));
        MatcherAssert.assertThat(vi.next(), Matchers.equalTo("4"));
        MatcherAssert.assertThat(vi.next(), Matchers.equalTo("5"));
        expectedEx.expect(NoSuchElementException.class);
        expectedEx.expectMessage("Error message");
        vi.next();
    }
}