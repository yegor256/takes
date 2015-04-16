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
 * Transform elements in an iterable into others.
 *
 * @author Yegor Bugayenko (yegor@teamed.io)
 * @version $Id$
 * @since 0.32.1
 */
public class IterableTransform<T> implements Iterable<T> {

	private final List<T> storage = new LinkedList<T>();
	
	public static interface TransformAction<T> {
		T transform(T element);
	}
	
	/**
	 * Transform elements in the supplied iterable by the action supplied.
	 * @param list
	 * @param action
	 */
	public IterableTransform(Iterable<T> list, IterableTransform.TransformAction<T> action) {
		Iterator<T> i = list.iterator();
		while(i.hasNext()) {
			this.storage.add(action.transform(i.next()));
		}
	}
	
	@Override
	public Iterator<T> iterator() {
		return this.storage.iterator();
	}

}
