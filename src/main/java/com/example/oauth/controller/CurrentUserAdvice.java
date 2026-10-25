package com.example.oauth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Makes the logged-in user available to every Thymeleaf page (null when not logged in).
@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute("currentUser")
    public OAuth2User currentUser(@AuthenticationPrincipal OAuth2User user) {
        return user;
    }
}
