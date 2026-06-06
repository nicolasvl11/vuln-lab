package com.bugvault.config;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * V7 — Security Misconfiguration (CWE-200 / A05:2021)
 *
 * Tests two aspects:
 * 1. Security headers — absent (vulnerable) vs present (secure)
 * 2. Actuator exposure — all endpoints open (vulnerable) vs restricted (secure)
 */
class MisconfigVulnTest {

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
            "vuln.misconfig=true",
            "management.endpoints.web.exposure.include=*"
    })
    class Vulnerable {

        @Autowired
        MockMvc mockMvc;

        @Test
        void exploit_succeeds_securityHeadersAbsent() throws Exception {
            mockMvc.perform(get("/api/_vuln/flags"))
                    .andExpect(status().isOk())
                    // Spring Security headers are disabled — none of these should be present
                    .andExpect(header().doesNotExist("X-Frame-Options"))
                    .andExpect(header().doesNotExist("X-Content-Type-Options"))
                    .andExpect(header().doesNotExist("Cache-Control"));
        }

        @Test
        void actuator_env_accessible() throws Exception {
            // All actuator endpoints exposed — env endpoint returns 200
            mockMvc.perform(get("/actuator/env"))
                    .andExpect(status().isOk());
        }

        @Test
        void actuator_beans_accessible() throws Exception {
            mockMvc.perform(get("/actuator/beans"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @TestPropertySource(properties = {
            "vuln.misconfig=false",
            "management.endpoints.web.exposure.include=health,info"
    })
    class Secure {

        @Autowired
        MockMvc mockMvc;

        @Test
        void exploit_blocked_securityHeadersPresent() throws Exception {
            mockMvc.perform(get("/api/_vuln/flags"))
                    .andExpect(status().isOk())
                    // Spring Security default headers apply
                    .andExpect(header().string("X-Frame-Options", "DENY"))
                    .andExpect(header().string("X-Content-Type-Options", "nosniff"));
        }

        @Test
        void actuator_env_notExposed() throws Exception {
            // Restricted to health + info — env must not be accessible
            mockMvc.perform(get("/actuator/env"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void actuator_health_stillAccessible() throws Exception {
            mockMvc.perform(get("/actuator/health"))
                    .andExpect(status().isOk());
        }
    }
}
