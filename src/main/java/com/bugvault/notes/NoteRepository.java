package com.bugvault.notes;

import com.bugvault.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByOwner(User owner);

    List<Note> findByVisibility(String visibility);

    List<Note> findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(String title, String body);
}
