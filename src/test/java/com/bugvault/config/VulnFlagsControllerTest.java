package com.bugvault.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VulnFlagsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getFlags_returns200() throws Exception {
        mockMvc.perform(get("/api/_vuln/flags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void getFlags_defaultModeIsVulnerable() throws Exception {
        mockMvc.perform(get("/api/_vuln/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mode").value("vulnerable"));
    }

    @Test
    void getFlags_allVulnsEnabledByDefault() throws Exception {
        mockMvc.perform(get("/api/_vuln/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sqli").value(true))
                .andExpect(jsonPath("$.commandInjection").value(true))
                .andExpect(jsonPath("$.pathTraversal").value(true))
                .andExpect(jsonPath("$.idor").value(true))
                .andExpect(jsonPath("$.xss").value(true))
                .andExpect(jsonPath("$.deserialization").value(true))
                .andExpect(jsonPath("$.jwtNone").value(true));
    }
}
