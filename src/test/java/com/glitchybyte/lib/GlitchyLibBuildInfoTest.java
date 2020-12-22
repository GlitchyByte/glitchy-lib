package com.glitchybyte.lib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GlitchyLibBuildInfoTest {

    @Test
    void hasBuildInfo() {
        assertNotNull(GlitchyLibBuildInfo.get());
    }
}
