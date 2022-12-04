// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gspring.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * This class provides async configuration.
 *
 * <p>At a minimum have this class in your configuration:
 * {@snippet :
 * @Configuration
 * @EnableAsync
 * public class AsyncConfiguration extends GAsyncConfiguration {
 *
 *     @Override
 *     @Bean(name = TASK_EXECUTOR_DEFAULT)
 *     public Executor getAsyncExecutor() {
 *         return super.getAsyncExecutor();
 *     }
 *
 *     @Bean(name = TASK_EXECUTOR_CONTROLLER)
 *     public Executor getControllerAsyncExecutor() {
 *         return super.getControllerAsyncExecutor();
 *     }
 * }
 * }
 */
public abstract class GAsyncConfiguration implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(GAsyncConfiguration.class);

    /**
     * Creates an async {@code AyncConfigurer} with sensible defaults.
     */
    public GAsyncConfiguration() {
        // No-op.
    }

    /**
     * Bean name for the default task executor.
     */
    public static final String TASK_EXECUTOR_DEFAULT = "taskExecutor";
    private static final String TASK_EXECUTOR_DEFAULT_PREFIX = "task";

    /**
     * Bean name for the controller task executor.
     */
    public static final String TASK_EXECUTOR_CONTROLLER = "controllerTaskExecutor";
    private static final String TASK_EXECUTOR_CONTROLLER_PREFIX = "controller";

    @Override
    public Executor getAsyncExecutor() {
        return createTaskExecutor(TASK_EXECUTOR_DEFAULT_PREFIX);
    }

    /**
     * Default {@code getControllerAsyncExecutor}. It creates an appropriate pool of threads for controller
     * async configurations. This method is to be called by the actual AsyncConfiguration class as explained
     * in the class description.
     *
     * @return An executor the async controller can use.
     */
    public Executor getControllerAsyncExecutor() {
        return createTaskExecutor(TASK_EXECUTOR_CONTROLLER_PREFIX);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /**
     * Creates a task executor with the given name prefix.
     *
     * @param namePrefix Thread name prefix.
     * @return An executor with sensible defaults.
     */
    protected Executor createTaskExecutor(final String namePrefix) {
        final int processors = Runtime.getRuntime().availableProcessors();
        final int corePool = 2;
        final int maxPool = Math.max(corePool, processors);
        final int capacity = corePool * 20;
        log.info("Creating '{}' = { core: {}, max: {}, capacity: {} }", namePrefix, corePool, maxPool, capacity);
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePool);
        executor.setMaxPoolSize(maxPool);
        executor.setQueueCapacity(capacity);
        executor.setThreadNamePrefix(namePrefix + "-");
        executor.initialize();
        return executor;
    }
}
