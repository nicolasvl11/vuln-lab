package com.bugvault.files;

import com.bugvault.config.VulnFlags;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStorageService {

    private final VulnFlags vulnFlags;
    private final String uploadDir;

    public FileStorageService(VulnFlags vulnFlags,
                              @Value("${app.upload-dir}") String uploadDir) {
        this.vulnFlags = vulnFlags;
        this.uploadDir = uploadDir;
    }

    @PostConstruct
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            log.debug("Created upload directory: {}", uploadDir);
        }
    }

    /**
     * V3 — Path Traversal (CWE-22)
     *
     * VULNERABLE: resolves filename directly with no sanitization.
     *   Input "../../etc/passwd" walks outside uploadDir.
     *
     * SECURE: canonicalizes the resolved path and verifies it stays
     *   within uploadDir. Throws SecurityException on traversal attempt.
     */
    public File resolveFilePath(String filename) throws IOException {
        if (vulnFlags.isPathTraversal()) {
            // VULNERABLE: simple string concatenation — no boundary check
            return new File(uploadDir + File.separator + filename);
        }

        // SECURE: normalize both paths (no I/O required) and assert containment
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path resolved = base.resolve(filename).normalize();

        if (!resolved.startsWith(base)) {
            log.warn("Path traversal blocked: {}", filename);
            throw new SecurityException("Path traversal detected: " + filename);
        }
        return resolved.toFile();
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
