/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.takes.http;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Runnable main method.
 * @since 0.32.5
 */
final class MainMethod implements Runnable {

    /**
     * Method.
     */
    private final Method method;

    /**
     * Additional arguments.
     */
    private final String[] passed;

    /**
     * Ctor.
     * @param method Main method
     * @param passed Additional arguments to be passed to the main method
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    MainMethod(final Method method, final String... passed) {
        this.method = method;
        this.passed = passed;
    }

    @Override
    public void run() {
        try {
            this.method.invoke(null, (Object) this.passed);
        } catch (final InvocationTargetException ex) {
            throw new IllegalStateException(
                new UncheckedText(
                    new FormattedText(
                        "The %s method has been invoked at an illegal time.",
                        this.method.getName()
                    )
                ).asString(), ex
            );
        } catch (final IllegalAccessException ex) {
            throw new IllegalStateException(
                new UncheckedText(
                    new FormattedText(
                        "The visibility of the %s method do not allow access.",
                        this.method.getName()
                    )
                ).asString(), ex
            );
        }
    }
}
