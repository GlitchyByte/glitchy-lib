// Copyright 2022 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.gspring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.concurrent.CompletableFuture;

/**
 * Static common request responses.
 */
public final class GRequestResponse {

    /**
     * Response with code: OK.
     */
    public static final CompletableFuture<ResponseEntity<StreamingResponseBody>>
            OK = createResponse(ResponseEntity.ok());

    /**
     * Response with code: ACCEPTED.
     */
    public static final CompletableFuture<ResponseEntity<StreamingResponseBody>>
            ACCEPTED = createResponse(ResponseEntity.accepted());

    /**
     * Response with code: BAD_REQUEST.
     */
    public static final CompletableFuture<ResponseEntity<StreamingResponseBody>>
            BAD_REQUEST = createResponse(ResponseEntity.badRequest());

    /**
     * Response with code: NOT_FOUND.
     */
    public static final CompletableFuture<ResponseEntity<StreamingResponseBody>>
            NOT_FOUND = createResponse(ResponseEntity.notFound());

    /**
     * Response with code: INTERNAL_SERVER_ERROR.
     */
    public static final CompletableFuture<ResponseEntity<StreamingResponseBody>>
            INTERNAL_SERVER_ERROR = createResponse(ResponseEntity.internalServerError());

    private static CompletableFuture<ResponseEntity<StreamingResponseBody>>
    createResponse(final ResponseEntity.HeadersBuilder<?> responseEntity) {
        return CompletableFuture.completedFuture(responseEntity.build());
    }

    private GRequestResponse() {
        // Hiding constructor.
    }
}
