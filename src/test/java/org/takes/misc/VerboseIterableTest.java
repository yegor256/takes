package org.takes.misc;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Test;

/**
 * 
 * @author marcus.sanchez (sanchez.marcus@gmail.com)
 * @version $Id$
 * @since 0.15.1
 */
public class VerboseIterableTest {

	private static final String NO_NEXT_VALUE_ERROR = "Empty Error Message";

	private final List<String> VALID_LIST = Arrays.asList("Accept: text/plain",
			"Accept-Charset: utf-8",
			"Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==",
			"Cache-Control: no-cache", "From: user@example.com");

	@Test
	public void getNewIterableWithCorrectSize() {
		MatcherAssert.assertThat(new VerboseIterable<String>(VALID_LIST,
				NO_NEXT_VALUE_ERROR), IsIterableWithSize
				.<String> iterableWithSize(VALID_LIST.size()));

	}
}
