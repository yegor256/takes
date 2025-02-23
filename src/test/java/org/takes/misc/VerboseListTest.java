/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.misc;

import java.util.Collections;
import java.util.List;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link VerboseList}.
 *
 * @since 0.32
 */
@SuppressWarnings("PMD.TooManyMethods")
@ExtendWith(MockitoExtension.class)
final class VerboseListTest {

    /**
     * Custom exception message.
     */
    private static final String MSG = "Error message";

    /**
     * Decorated List.
     */
    @Mock
    private List<Object> origin;

    /**
     * Decorator.
     */
    private VerboseList<Object> list;

    /**
     * Creates decorator.
     */
    @BeforeEach
    void setUp() {
        this.list = new VerboseList<>(
            this.origin,
            new TextOf(VerboseListTest.MSG)
        );
    }

    @Test
    void delegatesSize() {
        this.list.size();
        Mockito.verify(this.origin).size();
    }

    @Test
    void delegatesIsEmpty() {
        this.list.isEmpty();
        Mockito.verify(this.origin).isEmpty();
    }

    @Test
    void delegatesContains() {
        final Object obj = new Object();
        this.list.contains(obj);
        Mockito.verify(this.origin).contains(obj);
    }

    @Test
    void returnsVerboseIterator() {
        MatcherAssert.assertThat(
            this.list.iterator(),
            Matchers.instanceOf(VerboseIterator.class)
        );
    }

    @Test
    void delegatesToArray() {
        this.list.toArray();
        Mockito.verify(this.origin).toArray();
        final Object[] array = new Object[1];
        this.list.toArray(array);
        Mockito.verify(this.origin).toArray(array);
    }

    @Test
    void delegatesAdd() {
        final int index = 5;
        final Object obj = new Object();
        this.list.add(obj);
        Mockito.verify(this.origin).add(obj);
        this.list.add(index, obj);
        Mockito.verify(this.origin).add(index, obj);
    }

    @Test
    void delegatesRemove() {
        final int index = 5;
        final Object obj = new Object();
        this.list.remove(obj);
        Mockito.verify(this.origin).remove(obj);
        this.list.remove(index);
        Mockito.verify(this.origin).remove(index);
    }

    @Test
    void delegatesContainsAll() {
        final List<Object> collection = Collections.emptyList();
        this.list.containsAll(collection);
        Mockito.verify(this.origin).containsAll(collection);
    }

    @Test
    void delegatesAddAll() {
        final List<Object> collection = Collections.emptyList();
        this.list.addAll(collection);
        Mockito.verify(this.origin).addAll(collection);
        final int index = 5;
        this.list.addAll(index, collection);
        Mockito.verify(this.origin).addAll(index, collection);
    }

    @Test
    void delegatesRemoveAll() {
        final List<Object> collection = Collections.emptyList();
        this.list.removeAll(collection);
        Mockito.verify(this.origin).removeAll(collection);
    }

    @Test
    void delegatesRetainAll() {
        final List<Object> collection = Collections.emptyList();
        this.list.retainAll(collection);
        Mockito.verify(this.origin).retainAll(collection);
    }

    @Test
    void delegatesClear() {
        this.list.clear();
        Mockito.verify(this.origin).clear();
    }

    @Test
    void delegatesGet() {
        final int index = 5;
        this.list.get(index);
        Mockito.verify(this.origin).get(index);
    }

    @Test
    void delegatesSet() {
        final int index = 5;
        final Object obj = new Object();
        this.list.set(index, obj);
        Mockito.verify(this.origin).set(index, obj);
    }

    @Test
    void delegatesIndexOf() {
        final Object obj = new Object();
        this.list.indexOf(obj);
        Mockito.verify(this.origin).indexOf(obj);
    }

    @Test
    void delegatesLastIndexOf() {
        final Object obj = new Object();
        this.list.lastIndexOf(obj);
        Mockito.verify(this.origin).lastIndexOf(obj);
    }

    @Test
    void delegatesListIterator() {
        this.list.listIterator();
        Mockito.verify(this.origin).listIterator();
        final int index = 5;
        this.list.listIterator(index);
        Mockito.verify(this.origin).listIterator(index);
    }

    @Test
    void delegatesSubList() {
        final int from = 3;
        final int toidx = 5;
        this.list.subList(from, toidx);
        Mockito.verify(this.origin).subList(from, toidx);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromAddAll() {
        final int index = 5;
        final List<Object> collection = Collections.emptyList();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).addAll(index, collection);
        this.assertThat(() -> this.list.addAll(index, collection), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromGet() {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).get(index);
        this.assertThat(() -> this.list.get(index), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromSet() {
        final int index = 5;
        final Object obj = new Object();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).set(index, obj);
        this.assertThat(() -> this.list.set(index, obj), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromAdd() {
        final int index = 5;
        final Object obj = new Object();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).add(index, obj);
        this.assertThat(() -> this.list.add(index, obj), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromRemove() {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).remove(index);
        this.assertThat(() -> this.list.remove(index), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromListIterator() {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).listIterator(index);
        this.assertThat(() -> this.list.listIterator(index), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromSubList() {
        final int from = 2;
        final int toidx = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).subList(from, toidx);
        this.assertThat(() -> this.list.subList(from, toidx), cause);
    }

    /**
     * Assert cause.
     * @param exec Code block.
     * @param cause Cause.
     */
    private void assertThat(final Executable exec, final Exception cause) {
        MatcherAssert.assertThat(
            Assertions.assertThrows(IndexOutOfBoundsException.class, exec),
            Matchers.allOf(
                Matchers.hasProperty("message", Matchers.is(VerboseListTest.MSG)),
                Matchers.hasProperty("cause", Matchers.is(cause))
            )
        );
    }
}
