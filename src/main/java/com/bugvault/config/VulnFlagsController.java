package com.bugvault.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/_vuln")
public class VulnFlagsController {

    private final VulnFlags flags;

    public VulnFlagsController(VulnFlags flags) {
        this.flags = flags;
    }

    @GetMapping("/flags")
    public FlagsResponse getFlags() {
        return new FlagsResponse(
                flags.getMode(),
                flags.isSqli(),
                flags.isCommandInjection(),
                flags.isPathTraversal(),
                flags.isIdor(),
                flags.isXss(),
                flags.isDeserialization(),
                flags.isMisconfig(),
                flags.isJwtNone()
        );
    }

    public record FlagsResponse(
            String mode,
            boolean sqli,
            boolean commandInjection,
            boolean pathTraversal,
            boolean idor,
            boolean xss,
            boolean deserialization,
            boolean misconfig,
            boolean jwtNone
    ) {}
}
