package com.bugvault.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "vuln")
@Getter
@Setter
public class VulnFlags {

    private String mode = "vulnerable";
    private boolean sqli = true;
    private boolean commandInjection = true;
    private boolean pathTraversal = true;
    private boolean idor = true;
    private boolean xss = true;
    private boolean deserialization = true;
    private boolean misconfig = true;
    private boolean jwtNone = true;

    public boolean isVulnerableMode() {
        return "vulnerable".equalsIgnoreCase(mode);
    }

    public boolean isSecureMode() {
        return "secure".equalsIgnoreCase(mode);
    }
}
