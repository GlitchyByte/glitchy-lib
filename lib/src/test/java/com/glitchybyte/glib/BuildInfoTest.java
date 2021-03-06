// Copyright 2020-2021 GlitchyByte
// SPDX-License-Identifier: Apache-2.0

package com.glitchybyte.glib;

import com.glitchybyte.glib.mutable.GMutable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BuildInfoTest {

    @Test
    void canLoadBuildInfo() {
        final GMutable<BuildInfo> holder = new GMutable<>();
        assertDoesNotThrow(() -> holder.value = BuildInfo.loadFromResource(getClass()));
        final BuildInfo buildInfo = holder.value;
        assertNotNull(buildInfo.group);
        assertFalse(buildInfo.group.isBlank());
        assertNotNull(buildInfo.name);
        assertFalse(buildInfo.name.isBlank());
        assertNotNull(buildInfo.version);
        assertFalse(buildInfo.version.isBlank());
        assertNotNull(buildInfo.datetime);
        assertFalse(buildInfo.datetime.isBlank());
        assertNotNull(buildInfo.code);
        assertFalse(buildInfo.code.isBlank());
    }
}
