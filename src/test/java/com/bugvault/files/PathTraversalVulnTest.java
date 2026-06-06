package com.bugvault.files;

import com.bugvault.config.VulnFlags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * V3 — Path Traversal (CWE-22)
 *
 * Unit tests for FileStorageService path resolution.
 * No Spring context — tests the security logic directly.
 */
class PathTraversalVulnTest {

    @TempDir
    Path tempDir;

    VulnFlags vulnFlags;
    FileStorageService service;

    @BeforeEach
    void setup() {
        vulnFlags = new VulnFlags();
        service = new FileStorageService(vulnFlags, tempDir.toString());
    }

    @Nested
    class Vulnerable {

        @BeforeEach
        void enablePathTraversal() {
            vulnFlags.setPathTraversal(true);
        }

        @Test
        void exploit_succeeds_whenTraversalEnabled() throws IOException {
            // Traversal payload should resolve without exception
            assertThatCode(() -> service.resolveFilePath("../../etc/passwd"))
                    .doesNotThrowAnyException();
        }

        @Test
        void resolvedPath_escapesBoundary() throws IOException {
            File resolved = service.resolveFilePath("../../etc/passwd");
            // Path walks outside tempDir — no containment check
            assertThat(resolved.getCanonicalPath())
                    .doesNotStartWith(tempDir.toFile().getCanonicalPath());
        }

        @Test
        void normalFile_resolves() throws IOException {
            File resolved = service.resolveFilePath("test.txt");
            assertThat(resolved.getParentFile().getCanonicalPath())
                    .isEqualTo(tempDir.toFile().getCanonicalPath());
        }
    }

    @Nested
    class Secure {

        @BeforeEach
        void disablePathTraversal() {
            vulnFlags.setPathTraversal(false);
        }

        @Test
        void exploit_blocked_whenTraversalDisabled() {
            assertThatThrownBy(() -> service.resolveFilePath("../../etc/passwd"))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Path traversal");
        }

        @Test
        void windowsTraversal_blocked() {
            assertThatThrownBy(() -> service.resolveFilePath("..\\..\\windows\\system32\\drivers\\etc\\hosts"))
                    .isInstanceOf(SecurityException.class);
        }

        @Test
        void normalFile_stillResolves() throws IOException {
            File resolved = service.resolveFilePath("safe.txt");
            assertThat(resolved.toPath().getParent())
                    .isEqualTo(tempDir.toAbsolutePath().normalize());
        }

        @Test
        void absolutePathAsFilename_blocked() {
            assertThatThrownBy(() -> service.resolveFilePath("/etc/passwd"))
                    .isInstanceOf(SecurityException.class);
        }
    }
}
