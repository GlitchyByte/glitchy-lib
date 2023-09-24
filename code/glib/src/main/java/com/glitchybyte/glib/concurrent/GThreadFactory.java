// Copyright 2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib.concurrent;

import com.glitchybyte.glib.GStrings;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread factory for {@link GTaskExecutor}.
 *
 * <p>Produces platform threads.
 */
public final class GThreadFactory implements ThreadFactory {

    private static final String NAME_TEMPLATE = "Task-%s|%s";
    private static final AtomicLong factoryCount = new AtomicLong(0);

    private final long factoryId = factoryCount.getAndIncrement();
    private final AtomicLong threadCount = new AtomicLong(0);
    private final Thread.Builder.OfPlatform builder;

    /**
     * Creates the factory.
     */
    public GThreadFactory() {
        builder = Thread.ofPlatform();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Thread newThread(final Runnable runnable) {
        final String name = GStrings.format(NAME_TEMPLATE,
                Long.toHexString(factoryId),
                Long.toHexString(threadCount.getAndIncrement())
        );
        return builder.name(name).unstarted(runnable);
    }
}
