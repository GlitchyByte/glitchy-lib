// Copyright 2022-2023 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GResourcesTest {

    @Test
    void canReadResourceAsString() throws IOException {
        final String data = GResources.getResourceString(this, "res-test").trim();
        assertEquals("ok", data);
    }

    @Test
    void canReadVersion() {
        final String version = GResources.getVersion(this);
        assertNotNull(version);
    }
}
