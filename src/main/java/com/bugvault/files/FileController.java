package com.bugvault.files;

import com.bugvault.audit.AuditPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;
    private final AuditPublisher auditPublisher;

    /**
     * V3 — Path Traversal download endpoint (CWE-22)
     *
     * Vulnerable: GET /api/files/download?name=../../etc/passwd
     *   → returns contents of /etc/passwd
     *
     * Secure: path is canonicalized and confined to upload directory.
     *   → returns 403 Forbidden on traversal attempt
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam String name) {
        try {
            File file = fileStorageService.resolveFilePath(name);

            if (!file.exists() || !file.isFile()) {
                return ResponseEntity.notFound().build();
            }

            auditPublisher.publish("anonymous", "FILE_DOWNLOAD", name);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + file.getName() + "\"")
                    .body(new FileSystemResource(file));

        } catch (SecurityException e) {
            auditPublisher.publish("anonymous", "PATH_TRAVERSAL_BLOCKED", name);
            log.warn("Path traversal attempt blocked: {}", name);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        } catch (IOException e) {
            log.error("File resolution error for: {}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
