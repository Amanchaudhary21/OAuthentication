package com.example.oauth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NoteForm {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 2000, message = "Content must be at most 2000 characters")
    private String content;

    public NoteForm(Note note) {
        this.title = note.getTitle();
        this.content = note.getContent();
    }
}
