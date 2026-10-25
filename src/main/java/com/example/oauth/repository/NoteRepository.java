package com.example.oauth.repository;

import com.example.oauth.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByOwnerOrderByUpdatedAtDesc(String owner);

    Optional<Note> findByIdAndOwner(Long id, String owner);
}
