package de.sme;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sme.controller.UserApiController;
import de.sme.dto.NewsUserUpdateDTO;
import de.sme.entity.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserApiController.class)
class UserApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;
    @MockBean
    NewsUserRepository newsUserRepository;

    @Test
    void testFindAll() throws Exception {
        var testUser = new NewsUser("username", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        when(newsUserRepository.findAll()).thenReturn(List.of(testUser));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$[0].username")
                        .value(testUser.getUsername()))
                .andExpect((ResultMatcher) jsonPath("$[0].firstname")
                        .value(testUser.getFirstname()))
                .andExpect((ResultMatcher) jsonPath("$[0].lastname")
                        .value(testUser.getLastname()))
                .andExpect((ResultMatcher) jsonPath("$[0].birthday")
                        .value(testUser.getBirthday().toString()))
                .andExpect((ResultMatcher) jsonPath("$[0].password")
                        .doesNotExist());
        verify(newsUserRepository).findAll();
    }

    @Test
    void testFindAllPaged() throws Exception {
        var testUser = new NewsUser("username", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        PageRequest pageRequest = PageRequest.of(2, 10, Sort.by("birthday"));
        when(newsUserRepository.findAll(pageRequest))
                .thenReturn(new PageImpl(List.of(testUser)));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user?page=2&size=10&sort=birthday"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username")
                        .value(testUser.getUsername()))
                .andExpect(jsonPath("$[0].firstname")
                        .value(testUser.getFirstname()))
                .andExpect(jsonPath("$[0].lastname")
                        .value(testUser.getLastname()))
                .andExpect(jsonPath("$[0].birthday")
                        .value(testUser.getBirthday().toString()))
                .andExpect(jsonPath("$[0].password")
                        .doesNotExist());
        verify(newsUserRepository).findAll(pageRequest);
    }

    @Test
    void testFindById() throws Exception {
        var testUser = new NewsUser("username", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        when(newsUserRepository.findById(testUser.getUsername()))
                .thenReturn(Optional.of(testUser));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", testUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username")
                        .value(testUser.getUsername()))
                .andExpect(jsonPath("$.firstname")
                        .value(testUser.getFirstname()))
                .andExpect(jsonPath("$.lastname")
                        .value(testUser.getLastname()))
                .andExpect(jsonPath("$.birthday")
                        .value(testUser.getBirthday().toString()))
                .andExpect(jsonPath("$.password")
                        .doesNotExist());
        verify(newsUserRepository).findById(testUser.getUsername());
    }

    @Test
    void testInsertValidates() throws Exception {
        var testUser = new NewsUser("123", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        var json = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testNotFound() throws Exception {
        var username = "unknown";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", username))
                .andExpect(status().isNoContent());
        verify(newsUserRepository).findById(username);
    }

    @Test
    void testInsert() throws Exception {
        var testUser = new NewsUser("username", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        when(newsUserRepository.save(any())).thenReturn(testUser);
        var json = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(header()
                        .string("Location",
                                "http://localhost/api/user/" + testUser.getUsername()));
        var argumentCaptor =
                ArgumentCaptor.forClass(NewsUser.class);
        verify(newsUserRepository).save(argumentCaptor.capture());
        var savedUser = argumentCaptor.getValue();
        assertThat(testUser).usingRecursiveComparison()
                .ignoringFields("password", "version").isEqualTo(savedUser);
        assertThat(passwordEncoder.matches(
                testUser.getPassword(), savedUser.getPassword())).isTrue();
    }

    @Test
    void testUpdate() throws Exception {
        var testUser = new NewsUserUpdateDTO(
                "username",
                "pass",
                "first",
                "last",
                LocalDate.of(1970, 1, 1),
                1,
                "oldpass");
        var existingUser = new NewsUser(
                testUser.getUsername(),
                passwordEncoder.encode(testUser.getOldPassword()),
                "oldFirstName",
                "oldLastName",
                LocalDate.of(1970, 1, 1));
        var mockSavedUser = new NewsUser(
                testUser.getUsername(),
                passwordEncoder.encode(testUser.getPassword()),
                testUser.getFirstname(),
                testUser.getLastname(),
                testUser.getBirthday());
        when(newsUserRepository.findById(testUser.getUsername()))
                .thenReturn(Optional.of(existingUser));
        when(newsUserRepository.saveAndFlush(any())).thenReturn(mockSavedUser);
        var json = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/{username}", testUser.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
        var argumentCaptor =
                ArgumentCaptor.forClass(NewsUser.class);
        verify(newsUserRepository).saveAndFlush(argumentCaptor.capture());
        var savedUser = argumentCaptor.getValue();
        assertThat(testUser).usingRecursiveComparison()
                .ignoringFields("password", "version", "oldPassword").isEqualTo(savedUser);
        assertThat(passwordEncoder.matches(
                testUser.getPassword(), savedUser.getPassword())).isTrue();
    }

    @Test
    void testUpdateChecksPassword() throws Exception {
        var testUser = new NewsUserUpdateDTO("username", "pass",
                "first", "last",
                LocalDate.of(1970, 1, 1), 1, "oldpass");
        var existingUser = new NewsUser(testUser.getUsername(),
                passwordEncoder.encode("otherpass"), "oldFirstName", "oldLastName",
                LocalDate.of(1970, 1, 1));
        when(newsUserRepository.findById(testUser.getUsername()))
                .thenReturn(Optional.of(existingUser));
        var json = objectMapper.writeValueAsString(testUser);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/{username}", testUser.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
        verify(newsUserRepository, never()).saveAndFlush(any());
    }



    }
