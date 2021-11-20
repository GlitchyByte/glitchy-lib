// Copyright 2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gspring.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class StaticFileEndpointBase extends FileEndpointBase {

    private static final Logger log = LoggerFactory.getLogger(StaticFileEndpointBase.class);

    private final Map<Path, MediaType> pathMediaTypeMap = new HashMap<>();

    /**
     * Sets up the file endpoint.
     *
     * @param baseUri The base uri of requests. (e.g, "/web")
     * @param sourceRootPath The local file system source root.
     * @param maxAge Duration of http cache.
     */
    protected StaticFileEndpointBase(final String baseUri, final Path sourceRootPath, final Duration maxAge) {
        super(baseUri, sourceRootPath, CacheControl.maxAge(maxAge));
        List<Path> newDirectories = new ArrayList<>();
        try {
            Files.list(sourceRootPath).forEach(newDirectories::add);
            do {
                final List<Path> working = newDirectories;
                newDirectories = new ArrayList<>();
                for (final Path path : working) {
                    if (Files.isDirectory(path)) {
                        Files.list(path).forEach(newDirectories::add);
                        continue;
                    }
                    if (!Files.isRegularFile(path)) {
                        continue;
                    }
                    final MediaType mediaType = getMediaType(path);
                    if (mediaType == null) {
                        log.warn("MediaType not found for: {}", path);
                        continue;
                    }
                    pathMediaTypeMap.put(path, mediaType);
                }
            } while (!newDirectories.isEmpty());
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected CompletableFuture<ResponseEntity<StreamingResponseBody>> serveLocalPath(final Path localPath) {
        final MediaType mediaType = pathMediaTypeMap.get(localPath);
        if (mediaType == null) {
            return NOT_FOUND;
        }
        return streamingResponse(localPath, mediaType);
    }
}
