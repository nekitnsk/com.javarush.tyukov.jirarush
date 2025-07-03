package com.javarush.jira.profile.internal.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.ProfileMapper;
import com.javarush.jira.profile.internal.ProfileRepository;
import com.javarush.jira.profile.internal.model.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ProfileRestControllerTest extends AbstractControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileMapper profileMapper;

    @Test
    public void profileGetWithEmptyProfileData() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void profileGetWithWrongProfileData() throws Exception {
        mockMvc.perform(get("/api/profile").with(httpBasic("qwerty", "asdfdsa")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void profileGetWithAdminData() throws Exception {
        mockMvc.perform(get(ProfileRestController.REST_URL).with(httpBasic("admin@gmail.com", "admin")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.mailNotifications").isArray())
                .andExpect(jsonPath("$.contacts").isArray());
    }

    @Test
    public void profilePutWithoutAuthorizationTest() throws Exception {
        mockMvc.perform(put(ProfileRestController.REST_URL).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void profilePutWithAuthorization() throws Exception {
        ProfileTo profileTo = new ProfileTo(2L,
                Set.of("assigned", "deadline"),
                Set.of(new ContactTo("github", "newAdminGithub"))
        );
        mockMvc.perform(put(ProfileRestController.REST_URL).with(httpBasic("admin@gmail.com", "admin"))
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(objectMapper.writeValueAsString(profileTo)))
                        .andExpect(status().isNoContent());
        Profile profile = profileRepository.findById(2L).orElseThrow();

        ProfileTo actualProfile = profileMapper.toTo(profile);

        assertEquals(profileTo.getId(), actualProfile.getId());
        assertEquals(profileTo.getContacts(), actualProfile.getContacts());
        assertEquals(profileTo.getMailNotifications(), actualProfile.getMailNotifications());
    }


}