package com.bugvault.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final VulnFlags vulnFlags;

    /**
     * V7 — Security Misconfiguration (CWE-200 / A05:2021)
     *
     * VULNERABLE (misconfig=true):
     *   Security headers disabled — no X-Frame-Options, no X-Content-Type-Options,
     *   no Referrer-Policy. Combined with all-actuator exposure in application.yml.
     *
     * SECURE (misconfig=false):
     *   Spring Security default headers applied:
     *   X-Frame-Options: DENY, X-Content-Type-Options: nosniff,
     *   Cache-Control: no-cache, no-store, must-revalidate, etc.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        if (vulnFlags.isMisconfig()) {
            // VULNERABLE: strip all default security headers
            http.headers(AbstractHttpConfigurer::disable);
        }
        // else: Spring Security's default headers apply automatically

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Stub replaced by real UserService in Phase 2.
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> { throw new UsernameNotFoundException(username); };
    }
}
