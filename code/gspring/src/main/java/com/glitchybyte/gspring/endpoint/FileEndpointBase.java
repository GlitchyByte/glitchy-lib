// Copyright 2021-2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gspring.endpoint;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Base abstract class for serving local files.
 */
public abstract class FileEndpointBase {

    private final CompletableFuture<ResponseEntity<StreamingResponseBody>> redirectToRoot;
    private final Path sourceRootPath;
    private final CacheControl cacheControl;

    /**
     * Sets up the file endpoint.
     *
     * @param baseUri The base uri of requests. (e.g, "/web")
     * @param sourceRootPath The local file system source root.
     * @param cacheControl CacheControl to use for successful responses.
     */
    protected FileEndpointBase(final String baseUri, final Path sourceRootPath, final CacheControl cacheControl) {
        redirectToRoot = CompletableFuture.completedFuture(
                ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(baseUri + "/"))
                        .build()
        );
        this.sourceRootPath = sourceRootPath;
        this.cacheControl = cacheControl;
    }

    /**
     * Serves the given file. If the URI points to root, it will automatically append "index.html" to it.
     *
     * @param requestedUri URI to serve.
     * @return A streaming response payload.
     */
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> serveFileWithIndex(final String requestedUri) {
        if (requestedUri.isEmpty()) {
            return redirectToRoot;
        }
        if (requestedUri.equals("/")) {
            return serveFile("/index.html");
        }
        return serveFile(requestedUri);
    }

    /**
     * Serves the given file.
     *
     * @param requestedUri URI to serve.
     * @return A streaming response payload.
     */
    public CompletableFuture<ResponseEntity<StreamingResponseBody>> serveFile(final String requestedUri) {
        final Path localPath = sourceRootPath.resolve(requestedUri.substring(1));
        return serveLocalPath(localPath);
    }

    /**
     * Returns a streaming response of the given local file.
     *
     * <p>Implementation is dependent on the kind of endpoint: dynamic or static.
     *
     * @param localPath Local path to file to be transmitted.
     * @return A streaming response payload.
     */
    protected abstract CompletableFuture<ResponseEntity<StreamingResponseBody>> serveLocalPath(final Path localPath);

    /**
     * Creates a streaming response for the given path.
     *
     * @param localPath Local path to file to be transmitted.
     * @param mediaType Media type of file.
     * @return A streaming response payload.
     */
    protected CompletableFuture<ResponseEntity<StreamingResponseBody>> streamingResponse(final Path localPath, final MediaType mediaType) {
        final StreamingResponseBody stream = outputStream -> {
            try (final InputStream inputStream = Files.newInputStream(localPath)) {
                inputStream.transferTo(outputStream);
            }
        };
        return CompletableFuture.completedFuture(
                ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .contentType(mediaType)
                        .body(stream)
        );
    }
}
