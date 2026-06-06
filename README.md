# BugVault

> ⚠️ **WARNING: Deliberately Insecure Application**
>
> BugVault is an intentionally vulnerable Spring Boot application built for AppSec education and tool testing.
> **Never deploy this to a public network or production environment.**
> Bind to `127.0.0.1` only. Do not expose externally.

A realistic note-sharing SaaS clone containing 8 OWASP Top 10 vulnerabilities, each toggleable at runtime.
Part of the [vuln-lab](https://github.com/nicolasvl11/vuln-lab) project.

---

## OWASP Coverage

| # | Vulnerability | OWASP 2021 | CWE | Endpoint | Status |
|---|--------------|-----------|-----|---------|--------|
| V1 | SQL Injection | A03 | CWE-89 | `GET /api/notes/search?q=` | Phase 2 |
| V2 | OS Command Injection | A03 | CWE-78 | `POST /api/export` | Phase 2 |
| V3 | Path Traversal | A01 | CWE-22 | `GET /api/files/download?name=` | Phase 1 |
| V4 | IDOR / Broken Object-Level Auth | A01 | CWE-639 | `GET /api/notes/{id}` | Phase 2 |
| V5 | Reflected + Stored XSS | A03 | CWE-79 | Note title/body rendering | Phase 1 |
| V6 | Insecure Deserialization | A08 | CWE-502 | `POST /api/import` | Phase 3 |
| V7 | Security Misconfiguration | A05/A02 | CWE-200 | `/actuator/*` + hardcoded secret | Phase 1 |
| V8 | Broken JWT Authentication | A07 | CWE-287 | `POST /api/auth/login` | Phase 3 |

---

## Quick Start

```bash
git clone https://github.com/nicolasvl11/vuln-lab
cd vuln-lab/vuln-app
docker compose up
```

App runs at `http://localhost:8080`. API docs at `/swagger-ui.html` (added in Phase 2).

## Feature Flags

Every vulnerability is toggleable via environment variables or `application.yml`:

```yaml
vuln:
  mode: vulnerable   # "vulnerable" | "secure"
  sqli: true
  command-injection: true
  path-traversal: true
  idor: true
  xss: true
  deserialization: true
  misconfig: true
  jwt-none: true
```

**Disable a single vuln:**
```bash
VULN_SQLI=false docker compose up app
```

**Harden everything:**
```bash
VULN_MODE=secure docker compose up app
```

**Inspect current flags:**
```bash
curl http://localhost:8080/api/_vuln/flags
```

---

## Running with the Scanner

Once [sast-scanner-py](https://github.com/nicolasvl11/sast-scanner-py) is updated with DAST support:

```bash
# Static scan
python cli.py scan vuln-app/src --format html

# Dynamic scan (app must be running)
python cli.py dast http://localhost:8080 --format html

# Unified (SAST + DAST)
python cli.py unified --sast-path vuln-app/src --dast-url http://localhost:8080
```

---

## Project Structure

```
src/main/java/com/bugvault/
├── config/
│   ├── VulnFlags.java          # Feature flag configuration
│   ├── VulnFlagsController.java # GET /api/_vuln/flags
│   └── SecurityConfig.java
├── audit/
│   ├── AuditEvent.java
│   ├── AuditEventRepository.java
│   └── AuditPublisher.java
├── notes/                      # V1, V4, V5 (Phase 1-2)
├── files/                      # V3 (Phase 1)
├── export/                     # V2 (Phase 2)
├── importer/                   # V6 (Phase 3)
└── auth/                       # V8 + user management (Phase 2-3)
```

---

## Test Credentials (added in Phase 2)

| User | Password | Role |
|------|----------|------|
| alice | alice123 | USER |
| bob | bob456 | USER |
| admin | admin789 | ADMIN |

Bob has private notes that alice should not be able to read — the IDOR demo scenario.

---

## Responsible Use

This project is for:
- Learning application security concepts
- Testing SAST/DAST tooling
- Security training environments

Do not use this application to practice attacks against systems you do not own.
