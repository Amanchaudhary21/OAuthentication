package com.example.oauth.controller;

import com.example.oauth.model.Note;
import com.example.oauth.model.NoteForm;
import com.example.oauth.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal OAuth2User user, Model model) {
        model.addAttribute("notes", noteService.findAllByOwner(login(user)));
        return "notes/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("noteForm", new NoteForm());
        return "notes/form";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal OAuth2User user,
                         @Valid @ModelAttribute("noteForm") NoteForm form,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "notes/form";
        }
        Note note = noteService.create(form, login(user));
        redirect.addFlashAttribute("message", "Note created.");
        return "redirect:/notes/" + note.getId();
    }

    @GetMapping("/{id}")
    public String view(@AuthenticationPrincipal OAuth2User user, @PathVariable Long id, Model model) {
        model.addAttribute("note", noteService.findByIdAndOwner(id, login(user)));
        return "notes/view";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@AuthenticationPrincipal OAuth2User user, @PathVariable Long id, Model model) {
        Note note = noteService.findByIdAndOwner(id, login(user));
        model.addAttribute("noteId", id);
        model.addAttribute("noteForm", new NoteForm(note));
        return "notes/form";
    }

    @PostMapping("/{id}")
    public String update(@AuthenticationPrincipal OAuth2User user,
                         @PathVariable Long id,
                         @Valid @ModelAttribute("noteForm") NoteForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("noteId", id);
            return "notes/form";
        }
        noteService.update(id, form, login(user));
        redirect.addFlashAttribute("message", "Note updated.");
        return "redirect:/notes/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal OAuth2User user, @PathVariable Long id, RedirectAttributes redirect) {
        noteService.delete(id, login(user));
        redirect.addFlashAttribute("message", "Note deleted.");
        return "redirect:/notes";
    }

    private String login(OAuth2User user) {
        return user.getAttribute("login");
    }
}
