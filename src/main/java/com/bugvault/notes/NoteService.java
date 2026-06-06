package com.bugvault.notes;

import com.bugvault.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public Note create(User owner, String title, String body, String visibility) {
        return noteRepository.save(Note.builder()
                .owner(owner)
                .title(title)
                .body(body)
                .visibility(visibility)
                .build());
    }

    public Optional<Note> findById(Long id) {
        return noteRepository.findById(id);
    }

    public List<Note> findPublic() {
        return noteRepository.findByVisibility("PUBLIC");
    }

    /**
     * Safe JPA-derived query for P1. V1 SQLI (native query) added in Phase 2.
     */
    public List<Note> search(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return noteRepository.findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(query, query);
    }
}
