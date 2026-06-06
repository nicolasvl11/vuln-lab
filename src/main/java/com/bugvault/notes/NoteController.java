package com.bugvault.notes;

import com.bugvault.audit.AuditPublisher;
import com.bugvault.config.VulnFlags;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final VulnFlags vulnFlags;
    private final AuditPublisher auditPublisher;

    /**
     * V5 — Reflected XSS (CWE-79)
     *
     * Vulnerable: search query echoed with th:utext (unescaped HTML).
     *   GET /notes/search?q=<script>alert(1)</script>
     *   → script tag rendered in browser
     *
     * Secure: th:text (HTML-escaped output).
     *   → &lt;script&gt; appears as literal text
     */
    @GetMapping("/notes/search")
    public String search(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("searchQuery", q);
        model.addAttribute("vulnXss", vulnFlags.isXss());
        model.addAttribute("notes", noteService.search(q));
        auditPublisher.publish("anonymous", "NOTE_SEARCH", q);
        return "notes/search";
    }

    /**
     * V5 — Stored XSS (CWE-79)
     *
     * Vulnerable: note title/body rendered with th:utext.
     *   If a note was saved with XSS payload in title, it executes on view.
     *
     * Secure: th:text — payload stored but never executed on render.
     */
    @GetMapping("/notes/{id}")
    public String view(@PathVariable Long id, Model model) {
        Note note = noteService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("note", note);
        model.addAttribute("vulnXss", vulnFlags.isXss());
        auditPublisher.publish("anonymous", "NOTE_VIEW", String.valueOf(id));
        return "notes/view";
    }

    @GetMapping("/notes")
    public String list(Model model) {
        model.addAttribute("notes", noteService.findPublic());
        return "notes/list";
    }
}
