package com.example.oauth.controller;

import com.example.oauth.model.ContactForm;
import com.example.oauth.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public String form(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact/form";
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("contactForm") ContactForm form,
                         BindingResult result,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "contact/form";
        }
        contactService.save(form);
        redirect.addFlashAttribute("message", "Thanks " + form.getName() + ", your message was sent.");
        return "redirect:/contact";
    }

    @GetMapping("/messages")
    public String messages(Model model) {
        model.addAttribute("messages", contactService.findAll());
        return "contact/messages";
    }
}
