package com.example.oauth.controller;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class OauthController {


    @GetMapping()
    public String hello(){
        return "hello";
    }

    @GetMapping("/name")
    public String githubName(@AuthenticationPrincipal OAuth2User user){
        return user.getAttributes().get("login").toString();

    }
    @GetMapping("/attributes")
    public Map<String,Object> getAttributes(@AuthenticationPrincipal OAuth2User user){
        return user.getAttributes();
    }






}
