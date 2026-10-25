package com.example.oauth.service;

import com.example.oauth.model.Note;
import com.example.oauth.model.NoteForm;
import com.example.oauth.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> findAllByOwner(String owner) {
        return noteRepository.findByOwnerOrderByUpdatedAtDesc(owner);
    }

    // Returns 404 for notes that don't exist or belong to another user.
    public Note findByIdAndOwner(Long id, String owner) {
        return noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note not found"));
    }

    public Note create(NoteForm form, String owner) {
        LocalDateTime now = LocalDateTime.now();
        Note note = new Note();
        note.setOwner(owner);
        note.setTitle(form.getTitle());
        note.setContent(form.getContent());
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        return noteRepository.save(note);
    }

    @Transactional
    public Note update(Long id, NoteForm form, String owner) {
        Note note = findByIdAndOwner(id, owner);
        note.setTitle(form.getTitle());
        note.setContent(form.getContent());
        note.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    @Transactional
    public void delete(Long id, String owner) {
        noteRepository.delete(findByIdAndOwner(id, owner));
    }
}
