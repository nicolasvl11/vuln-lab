package com.bugvault.notes;

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
 * V5 — Reflected XSS (CWE-79)
 *
 * Tests the /notes/search endpoint which echoes the query parameter
 * into the HTML response. Verifies th:utext (vulnerable) vs th:text (secure).
 */
class XssVulnTest {

    static final String XSS_PAYLOAD = "<script>alert(1)</script>";
    static final String XSS_ESCAPED = "&lt;script&gt;alert(1)&lt;/script&gt;";

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @TestPropertySource(properties = "vuln.xss=true")
    class Vulnerable {

        @Autowired
        MockMvc mockMvc;

        @Test
        void exploit_succeeds_xssPayloadReflectedUnescaped() throws Exception {
            mockMvc.perform(get("/notes/search").param("q", XSS_PAYLOAD))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/html"))
                    // th:utext renders payload as raw HTML — script tag present
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(XSS_PAYLOAD)));
        }

        @Test
        void emptyQuery_renders200() throws Exception {
            mockMvc.perform(get("/notes/search"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @SpringBootTest
    @AutoConfigureMockMvc
    @ActiveProfiles("test")
    @TestPropertySource(properties = "vuln.xss=false")
    class Secure {

        @Autowired
        MockMvc mockMvc;

        @Test
        void exploit_blocked_xssPayloadHtmlEscaped() throws Exception {
            mockMvc.perform(get("/notes/search").param("q", XSS_PAYLOAD))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith("text/html"))
                    // th:text escapes — raw script tag must NOT appear
                    .andExpect(content().string(
                            org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString(XSS_PAYLOAD))))
                    // Escaped version appears instead
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(XSS_ESCAPED)));
        }

        @Test
        void htmlEntities_escapedInOutput() throws Exception {
            mockMvc.perform(get("/notes/search").param("q", "<b>bold</b>"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(
                            org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("<b>bold</b>"))))
                    .andExpect(content().string(
                            org.hamcrest.Matchers.containsString("&lt;b&gt;bold&lt;/b&gt;")));
        }
    }
}
