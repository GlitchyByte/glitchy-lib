// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gspring.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * This class sets up async configuration.
 *
 * <p>At a minimum have this class in your configuration:
 * {@snippet :
 * @Configuration
 * public class WebMvcConfiguration extends GWebMvcConfiguration {
 *
 *     @Autowired
 *     protected WebMvcConfiguration(final Executor taskExecutor) {
 *         super(taskExecutor);
 *     }
 * }
 * }
 */
public abstract class GWebMvcConfiguration extends WebMvcConfigurationSupport {

    private static final Logger log = LoggerFactory.getLogger(GWebMvcConfiguration.class);

    private final Executor taskExecutor;

    /**
     * Creates an async {@code WebMvcConfigurationSupport} with sensible defaults.
     *
     * @param taskExecutor Executor to handle requests asynchronously.
     */
    protected GWebMvcConfiguration(final Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    protected void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor((AsyncTaskExecutor) taskExecutor)
                .setDefaultTimeout(30_000)
                .registerCallableInterceptors(getCallableProcessingInterceptor());
        super.configureAsyncSupport(configurer);
    }

    /**
     * Timeout handler.
     *
     * <p>This handler simply logs the event.
     *
     * @return The timeout handler.
     */
    protected CallableProcessingInterceptor getCallableProcessingInterceptor() {
        return new TimeoutCallableProcessingInterceptor() {
            @Override
            public <T> Object handleTimeout(final NativeWebRequest request, final Callable<T> task) throws Exception {
                log.error("Timeout request: {}", request.getContextPath());
                return super.handleTimeout(request, task);
            }
        };
    }
}
