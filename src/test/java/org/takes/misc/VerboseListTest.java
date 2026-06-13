/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package org.takes.misc;

import java.util.Collections;
import java.util.List;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link VerboseList}.
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

    @Test
    void delegatesSize() {
        Mockito.when(this.origin.size()).thenReturn(7);
        MatcherAssert.assertThat(
            "size must be delegated to origin",
            this.verboseList().size(),
            Matchers.is(7)
        );
    }

    @Test
    void delegatesIsEmpty() {
        Mockito.when(this.origin.isEmpty()).thenReturn(true);
        MatcherAssert.assertThat(
            "isEmpty must be delegated to origin",
            this.verboseList().isEmpty(),
            Matchers.is(true)
        );
    }

    @Test
    void delegatesContains() {
        final Object obj = new Object();
        Mockito.when(this.origin.contains(obj)).thenReturn(true);
        MatcherAssert.assertThat(
            "contains must be delegated to origin",
            this.verboseList().contains(obj),
            Matchers.is(true)
        );
    }

    @Test
    void returnsVerboseIterator() {
        MatcherAssert.assertThat(
            "List iterator must be wrapped in VerboseIterator",
            this.verboseList().iterator(),
            Matchers.instanceOf(VerboseIterator.class)
        );
    }

    @Test
    void delegatesToArrayNoArgs() {
        final Object[] expected = {new Object(), new Object()};
        Mockito.when(this.origin.toArray()).thenReturn(expected);
        MatcherAssert.assertThat(
            "toArray must be delegated to origin",
            this.verboseList().toArray(),
            Matchers.is(expected)
        );
    }

    @Test
    void delegatesToArrayWithArg() {
        final Object[] array = new Object[1];
        this.verboseList().toArray(array);
        Mockito.verify(this.origin).toArray(array);
    }

    @Test
    void delegatesAddObject() {
        final Object obj = new Object();
        this.verboseList().add(obj);
        Mockito.verify(this.origin).add(obj);
    }

    @Test
    void delegatesAddAtIndex() {
        final int index = 5;
        final Object obj = new Object();
        this.verboseList().add(index, obj);
        Mockito.verify(this.origin).add(index, obj);
    }

    @Test
    void delegatesRemoveObject() {
        final Object obj = new Object();
        this.verboseList().remove(obj);
        Mockito.verify(this.origin).remove(obj);
    }

    @Test
    void delegatesRemoveAtIndex() {
        final int index = 5;
        this.verboseList().remove(index);
        Mockito.verify(this.origin).remove(index);
    }

    @Test
    void delegatesContainsAll() {
        final List<Object> collection = Collections.emptyList();
        Mockito.when(this.origin.containsAll(collection)).thenReturn(true);
        MatcherAssert.assertThat(
            "containsAll must be delegated to origin",
            this.verboseList().containsAll(collection),
            Matchers.is(true)
        );
    }

    @Test
    void delegatesAddAllCollection() {
        final List<Object> collection = Collections.emptyList();
        this.verboseList().addAll(collection);
        Mockito.verify(this.origin).addAll(collection);
    }

    @Test
    void delegatesAddAllAtIndex() {
        final List<Object> collection = Collections.emptyList();
        final int index = 5;
        this.verboseList().addAll(index, collection);
        Mockito.verify(this.origin).addAll(index, collection);
    }

    @Test
    void delegatesRemoveAll() {
        final List<Object> collection = Collections.emptyList();
        this.verboseList().removeAll(collection);
        Mockito.verify(this.origin).removeAll(collection);
    }

    @Test
    void delegatesRetainAll() {
        final List<Object> collection = Collections.emptyList();
        this.verboseList().retainAll(collection);
        Mockito.verify(this.origin).retainAll(collection);
    }

    @Test
    void delegatesClear() {
        this.verboseList().clear();
        Mockito.verify(this.origin).clear();
    }

    @Test
    void delegatesGet() {
        final int index = 5;
        this.verboseList().get(index);
        Mockito.verify(this.origin).get(index);
    }

    @Test
    void delegatesSet() {
        final int index = 5;
        final Object obj = new Object();
        this.verboseList().set(index, obj);
        Mockito.verify(this.origin).set(index, obj);
    }

    @Test
    void delegatesIndexOf() {
        final Object obj = new Object();
        this.verboseList().indexOf(obj);
        Mockito.verify(this.origin).indexOf(obj);
    }

    @Test
    void delegatesLastIndexOf() {
        final Object obj = new Object();
        this.verboseList().lastIndexOf(obj);
        Mockito.verify(this.origin).lastIndexOf(obj);
    }

    @Test
    void delegatesListIteratorNoArgs() {
        this.verboseList().listIterator();
        Mockito.verify(this.origin).listIterator();
    }

    @Test
    void delegatesListIteratorAtIndex() {
        final int index = 5;
        this.verboseList().listIterator(index);
        Mockito.verify(this.origin).listIterator(index);
    }

    @Test
    void delegatesSubList() {
        final int from = 3;
        final int toidx = 5;
        this.verboseList().subList(from, toidx);
        Mockito.verify(this.origin).subList(from, toidx);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromAddAll() {
        final int index = 5;
        final List<Object> collection = Collections.emptyList();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).addAll(index, collection);
        this.assertThat(
            () -> this.verboseList().addAll(index, collection),
            cause
        );
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromGet() {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).get(index);
        this.assertThat(() -> this.verboseList().get(index), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromSet() {
        final int index = 5;
        final Object obj = new Object();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).set(index, obj);
        this.assertThat(() -> this.verboseList().set(index, obj), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromAdd() {
        final int index = 5;
        final Object obj = new Object();
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).add(index, obj);
        this.assertThat(() -> this.verboseList().add(index, obj), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromRemove() {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).remove(index);
        this.assertThat(() -> this.verboseList().remove(index), cause);
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromListIterator() {
        final int index = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).listIterator(index);
        this.assertThat(
            () -> this.verboseList().listIterator(index),
            cause
        );
    }

    @Test
    void wrapsIndexOutOfBoundsExceptionFromSubList() {
        final int from = 2;
        final int toidx = 5;
        final Exception cause = new IndexOutOfBoundsException();
        Mockito.doThrow(cause).when(this.origin).subList(from, toidx);
        this.assertThat(
            () -> this.verboseList().subList(from, toidx),
            cause
        );
    }

    /**
     * Build a fresh decorated list using the mocked origin.
     * @return Decorator wrapping the mock origin
     */
    private VerboseList<Object> verboseList() {
        return new VerboseList<>(
            this.origin,
            new TextOf(VerboseListTest.MSG)
        );
    }

    /**
     * Assert cause.
     * @param exec Code block
     * @param cause Cause
     */
    private void assertThat(final Executable exec, final Exception cause) {
        MatcherAssert.assertThat(
            "Exception must have expected message and cause",
            Assertions.assertThrows(IndexOutOfBoundsException.class, exec),
            Matchers.allOf(
                Matchers.hasProperty("message", Matchers.is(VerboseListTest.MSG)),
                Matchers.hasProperty("cause", Matchers.is(cause))
            )
        );
    }
}
