package org.takes.misc;

import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author marcus.sanchez (sanchez.marcus@gmail.com)
 * @version $Id$
 * @since 0.15.1
 */
public class VerboseIteratorTest {

	private static final String NO_NEXT_VALUE_ERROR = "Empty Error Message";

	private final List<String> VALID_LIST = Arrays.asList("Accept: text/plain",
			"Accept-Charset: utf-8",
			"Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
			"Cache-Control: no-cache", "From: user@example.com");

	private final List<String> EMPTY_LIST = Arrays.asList();

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void getNextValue() {
		MatcherAssert.assertThat(new VerboseIterable<String>(VALID_LIST,
				NO_NEXT_VALUE_ERROR).iterator().next(),
				equalTo("Accept: text/plain"));
	}

	@Test
	public void hasNextValue() {
		MatcherAssert.assertThat(new VerboseIterable<String>(VALID_LIST, NO_NEXT_VALUE_ERROR)
				.iterator().hasNext(), equalTo(true));
	}

	@Test
	public void getNextValueOnEmptyList() {
		thrown.expectMessage(equalTo(NO_NEXT_VALUE_ERROR));
		new VerboseIterable<String>(EMPTY_LIST, NO_NEXT_VALUE_ERROR).iterator()
				.next();
	}

	@Test
	public void hasNextValueOnEmptyList() {
		MatcherAssert.assertThat(new VerboseIterable<String>(EMPTY_LIST,
				NO_NEXT_VALUE_ERROR).iterator().hasNext(), equalTo(false));
	}

	@Test
	// @todo check if the remove value should be implemented, it
	public void removeValue() {
		// Remove if verboseIterable.iterator().remove() is implemented
		thrown.expect(UnsupportedOperationException.class);
		VerboseIterable<String> verboseIterable = new VerboseIterable<String>(
				VALID_LIST, NO_NEXT_VALUE_ERROR);
		verboseIterable.iterator().remove();
		MatcherAssert.assertThat(verboseIterable, IsIterableWithSize
				.<String> iterableWithSize(VALID_LIST.size() - 1));
	}

}
