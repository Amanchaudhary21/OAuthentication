package com.example.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebPagesTests {

    @Autowired
    private MockMvc mvc;

    private RequestPostProcessor githubUser(String login) {
        return oauth2Login().attributes(attrs -> {
            attrs.put("login", login);
            attrs.put("avatar_url", "https://example.com/avatar.png");
            attrs.put("html_url", "https://github.com/" + login);
        });
    }

    @Test
    void homeIsPublic() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login with GitHub")));
    }

    @Test
    void notesRequireLogin() throws Exception {
        mvc.perform(get("/notes"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void profileShowsGithubLogin() throws Exception {
        mvc.perform(get("/profile").with(githubUser("octocat")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("octocat")));
    }

    @Test
    void createViewEditAndDeleteNote() throws Exception {
        String location = mvc.perform(post("/notes").with(githubUser("alice")).with(csrf())
                        .param("title", "Shopping list")
                        .param("content", "Milk"))
                .andExpect(status().is3xxRedirection())
                .andReturn().getResponse().getRedirectedUrl();

        mvc.perform(get(location).with(githubUser("alice")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Shopping list")));

        mvc.perform(get(location + "/edit").with(githubUser("alice")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Edit Note")));

        mvc.perform(post(location).with(githubUser("alice")).with(csrf())
                        .param("title", "Groceries")
                        .param("content", "Milk, eggs"))
                .andExpect(redirectedUrl(location));

        mvc.perform(get("/notes").with(githubUser("alice")))
                .andExpect(content().string(containsString("Groceries")));

        // Another user cannot see Alice's note
        mvc.perform(get(location).with(githubUser("bob")))
                .andExpect(status().isNotFound());

        mvc.perform(post(location + "/delete").with(githubUser("alice")).with(csrf()))
                .andExpect(redirectedUrl("/notes"));

        mvc.perform(get(location).with(githubUser("alice")))
                .andExpect(status().isNotFound());
    }

    @Test
    void blankNoteTitleShowsError() throws Exception {
        mvc.perform(post("/notes").with(githubUser("alice")).with(csrf())
                        .param("title", "")
                        .param("content", "text"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("noteForm", "title"))
                .andExpect(content().string(containsString("Title is required")));
    }

    @Test
    void contactFormValidatesAndSubmits() throws Exception {
        mvc.perform(post("/contact").with(csrf())
                        .param("name", "Sam")
                        .param("email", "not-an-email")
                        .param("message", "Hi"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Enter a valid email address")));

        mvc.perform(post("/contact").with(csrf())
                        .param("name", "Sam")
                        .param("email", "sam@example.com")
                        .param("message", "Hi there"))
                .andExpect(redirectedUrl("/contact"));

        mvc.perform(get("/contact/messages").with(githubUser("alice")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sam@example.com")));
    }

    @Test
    void apiReturnsGithubName() throws Exception {
        mvc.perform(get("/api/name").with(githubUser("octocat")))
                .andExpect(status().isOk())
                .andExpect(content().string("octocat"));
    }
}
