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
package org.takes.misc;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.*;

import org.junit.Test;

/**
 * Test case for {@link Concat}.
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.32.1
 */
public class ConcatTest {
	
	@Test
	public void concat() {
		List<String> a = new ArrayList<String>();
		a.add("a1");
		a.add("a2");
		List<String> b = new ArrayList<String>();
		b.add("b1");
		b.add("b2");
		MatcherAssert.assertThat((Iterable<String>)new Concat<String>(a,b), hasItems("a1", "a2", "b1", "b2"));
	}
	
	@Test
	public void concatWithEmpty() {
		List<String> a = new ArrayList<String>();
		a.add("a1");
		a.add("a2");
		List<String> b = new ArrayList<String>();
		
		MatcherAssert.assertThat((Iterable<String>)new Concat<String>(a,b), hasItems("a1", "a2"));
		MatcherAssert.assertThat((Iterable<String>)new Concat<String>(a,b), not(hasItems("")));
		//ensure concat empty lists will be empty
		MatcherAssert.assertThat((Iterable<String>)new Concat<String>(b,b), emptyIterable());
	}
	
	@Test
	public void concatWithCondition() {
		List<String> a = new ArrayList<String>();
		a.add("a1");
		a.add("a2");
		List<String> b = new ArrayList<String>();
		b.add("b1");
		b.add("b2");
		
		Iterable<String> result = new Concat<String>(a,b, new Concat.Condition<String>() {

			@Override
			public boolean add(String element) {
				return element.endsWith("1");
			}
			
		});
		
		MatcherAssert.assertThat(result, hasItems("a1", "b1"));
		MatcherAssert.assertThat(result, not(hasItems("a2", "b2")));
	}

}
