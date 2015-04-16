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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Concat iterable.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.32.1
 */
public final class Concat<T> implements Iterable<T> {

	private final List<T> storage = new LinkedList<T>();
	
	public static interface Condition<T> {
		/**
		 * Determine if an element should be added.
		 * @param element
		 * @return
		 */
		boolean add(T element);
	}
	
	/**
	 * To produce an iterable collection combining a and b, with order of the elements in a first.
	 * @param a
	 * @param b
	 */
	public Concat(Iterable<T> a, Iterable<T> b) {
		concat(a);
		concat(b);
	}
	
	/**
	 * To produce an iterable collection, determined by condition, combining a and b, with order of the elements in a first.
	 * @param a
	 * @param b
	 * @param cond
	 */
	public Concat(Iterable<T> a, Iterable<T> b, Condition<T> cond) {
		concat(a, cond);
		concat(b, cond);
	}

	private void concat(Iterable<T> a, Condition<T> cond) {
		Iterator<T> i = a.iterator();
		while(i.hasNext()) {
			T element = i.next();
			if(cond.add(element)) {
				this.storage.add(element);
			}
		}
	}

	private void concat(Iterable<T> a) {
		Iterator<T> i = a.iterator();
		while(i.hasNext()) {
			this.storage.add(i.next());
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return this.storage.iterator();
	}

}
