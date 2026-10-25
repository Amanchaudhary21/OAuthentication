package com.example.oauth.service;

import com.example.oauth.model.ContactForm;
import com.example.oauth.model.ContactMessage;
import com.example.oauth.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public void save(ContactForm form) {
        ContactMessage message = new ContactMessage();
        message.setName(form.getName());
        message.setEmail(form.getEmail());
        message.setMessage(form.getMessage());
        message.setCreatedAt(LocalDateTime.now());
        contactMessageRepository.save(message);
    }

    public List<ContactMessage> findAll() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }
}
