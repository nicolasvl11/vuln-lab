package com.bugvault.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VulnFlagsTest {

    @Test
    void defaultMode_isVulnerable() {
        VulnFlags flags = new VulnFlags();
        assertThat(flags.isVulnerableMode()).isTrue();
        assertThat(flags.isSecureMode()).isFalse();
    }

    @Test
    void allVulns_enabledByDefault() {
        VulnFlags flags = new VulnFlags();
        assertThat(flags.isSqli()).isTrue();
        assertThat(flags.isCommandInjection()).isTrue();
        assertThat(flags.isPathTraversal()).isTrue();
        assertThat(flags.isIdor()).isTrue();
        assertThat(flags.isXss()).isTrue();
        assertThat(flags.isDeserialization()).isTrue();
        assertThat(flags.isMisconfig()).isTrue();
        assertThat(flags.isJwtNone()).isTrue();
    }

    @Test
    void secureMode_flipsVulnerableMode() {
        VulnFlags flags = new VulnFlags();
        flags.setMode("secure");
        assertThat(flags.isVulnerableMode()).isFalse();
        assertThat(flags.isSecureMode()).isTrue();
    }

    @Test
    void individualFlag_canBeDisabled() {
        VulnFlags flags = new VulnFlags();
        flags.setSqli(false);
        assertThat(flags.isSqli()).isFalse();
        assertThat(flags.isIdor()).isTrue();
    }

    @Test
    void modeCheck_isCaseInsensitive() {
        VulnFlags flags = new VulnFlags();
        flags.setMode("VULNERABLE");
        assertThat(flags.isVulnerableMode()).isTrue();

        flags.setMode("SECURE");
        assertThat(flags.isSecureMode()).isTrue();
    }
}
