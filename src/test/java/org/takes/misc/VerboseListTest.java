/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2018 Yegor Bugayenko
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

import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests for {@link VerboseList}.
 *
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 * @since 0.32
 */
@SuppressWarnings("PMD.TooManyMethods")
@RunWith(MockitoJUnitRunner.class)
public class VerboseListTest {

    /**
     * Custom exception message.
     */
    private static final String MSG = "Error message";

    /**
     * Rule for asserting thrown exceptions.
     */
    @Rule
    public final transient ExpectedException thrown = ExpectedException.none();

    /**
     * Decorated List.
     */
    @Mock
    private transient List<Object> origin;

    /**
     * Decorator.
     */
    private transient VerboseList<Object> list;

    /**
     * Creates decorator.
     * @throws Exception If something goes wrong.
     */
    @Before
    public final void setUp() throws Exception {
        this.list = new VerboseList<Object>(this.origin, VerboseListTest.MSG);
    }

    /**
     * VerboseList should delegate size method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesSize() throws Exception {
        this.list.size();
        Mockito.verify(this.origin).size();
    }

    /**
     * VerboseList should delegate isEmpty method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesIsEmpty() throws Exception {
        this.list.isEmpty();
        Mockito.verify(this.origin).isEmpty();
    }

    /**
     * VerboseList should delegate contains method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesContains() throws Exception {
        final Object obj = new Object();
        this.list.contains(obj);
        Mockito.verify(this.origin).contains(obj);
    }

    /**
     * VerboseList should return {@link VerboseIterator}.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void returnsVerboseIterator() throws Exception {
        MatcherAssert.assertThat(
            this.list.iterator(),
            Matchers.instanceOf(VerboseIterator.class)
        );
    }

    /**
     * VerboseList should delegate toArray method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesToArray() throws Exception {
        this.list.toArray();
        Mockito.verify(this.origin).toArray();
        final Object[] array = new Object[1];
        this.list.toArray(array);
        Mockito.verify(this.origin).toArray(array);
    }

    /**
     * VerboseList should delegate add method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesAdd() throws Exception {
        final int index = 5;
        final Object obj = new Object();
        this.list.add(obj);
        Mockito.verify(this.origin).add(obj);
        this.list.add(index, obj);
        Mockito.verify(this.origin).add(index, obj);
    }

    /**
     * VerboseList should delegate remove method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesRemove() throws Exception {
        final int index = 5;
        final Object obj = new Object();
        this.list.remove(obj);
        Mockito.verify(this.origin).remove(obj);
        this.list.remove(index);
        Mockito.verify(this.origin).remove(index);
    }

    /**
     * VerboseList should delegate containsAll method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesContainsAll() throws Exception {
        final List<Object> collection = Collections.emptyList();
        this.list.containsAll(collection);
        Mockito.verify(this.origin).containsAll(collection);
    }

    /**
     * VerboseList should delegate addAll method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesAddAll() throws Exception {
        final List<Object> collection = Collections.emptyList();
        this.list.addAll(collection);
        Mockito.verify(this.origin).addAll(collection);
        final int index = 5;
        this.list.addAll(index, collection);
        Mockito.verify(this.origin).addAll(index, collection);
    }

    /**
     * VerboseList should delegate removeAll method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesRemoveAll() throws Exception {
        final List<Object> collection = Collections.emptyList();
        this.list.removeAll(collection);
        Mockito.verify(this.origin).removeAll(collection);
    }

    /**
     * VerboseList should delegate retainAll method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesRetainAll() throws Exception {
        final List<Object> collection = Collections.emptyList();
        this.list.retainAll(collection);
        Mockito.verify(this.origin).retainAll(collection);
    }

    /**
     * VerboseList should delegate clear method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesClear() throws Exception {
        this.list.clear();
        Mockito.verify(this.origin).clear();
    }

    /**
     * VerboseList should delegate get method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesGet() throws Exception {
        final int index = 5;
        this.list.get(index);
        Mockito.verify(this.origin).get(index);
    }

    /**
     * VerboseList should delegate set method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesSet() throws Exception {
        final int index = 5;
        final Object obj = new Object();
        this.list.set(index, obj);
        Mockito.verify(this.origin).set(index, obj);
    }

    /**
     * VerboseList should delegate indexOf method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesIndexOf() throws Exception {
        final Object obj = new Object();
        this.list.indexOf(obj);
        Mockito.verify(this.origin).indexOf(obj);
    }

    /**
     * VerboseList should delegate lastIndexOf method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesLastIndexOf() throws Exception {
        final Object obj = new Object();
        this.list.lastIndexOf(obj);
        Mockito.verify(this.origin).lastIndexOf(obj);
    }

    /**
     * VerboseList should delegate listIterator method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesListIterator() throws Exception {
        this.list.listIterator();
        Mockito.verify(this.origin).listIterator();
        final int index = 5;
        this.list.listIterator(index);
        Mockito.verify(this.origin).listIterator(index);
    }

    /**
     * VerboseList should delegate subList method to decorated List.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void delegatesSubList() throws Exception {
        final int from = 3;
        final int toidx = 5;
        this.list.subList(from, toidx);
        Mockito.verify(this.origin).subList(from, toidx);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by addAll method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromAddAll()
        throws Exception {
        final int index = 5;
        final List<Object> collection = Collections.emptyList();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).addAll(index, collection);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.addAll(index, collection);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by get method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromGet() throws Exception {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).get(index);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.get(index);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by set method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromSet() throws Exception {
        final int index = 5;
        final Object obj = new Object();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).set(index, obj);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.set(index, obj);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by add method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromAdd() throws Exception {
        final int index = 5;
        final Object obj = new Object();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).add(index, obj);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.add(index, obj);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by remove method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromRemove()
        throws Exception {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).remove(index);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.remove(index);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by listIterator
     * method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromListIterator()
        throws Exception {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).listIterator(index);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.listIterator(index);
    }

    /**
     * VerboseList should wraps OutOfBoundsException thrown by subList method.
     * @throws Exception If something goes wrong.
     */
    @Test
    public final void wrapsIndexOutOfBoundsExceptionFromSubList()
        throws Exception {
        final int from = 2;
        final int toidx = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).subList(from, toidx);
        this.thrown.expect(IndexOutOfBoundsException.class);
        this.thrown.expectMessage(VerboseListTest.MSG);
        this.thrown.expectCause(Matchers.is(cause));
        this.list.subList(from, toidx);
    }
}
