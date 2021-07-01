package de.sme;

import de.sme.controller.RegistrationController;
import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @MockBean
    NewsUserRepository newsUserRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/register"))
                .andExpect(model().attributeExists("newsUser"))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("Register")));
    }
    @Test
    void testProcessRegistrationForm() throws Exception {
        var checkUser = new de.sme.entity.NewsUser("user", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));

        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/register").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstname", checkUser.getFirstname())
                        .param("lastname", checkUser.getLastname())
                        .param("birthday",
                                checkUser.getBirthday().format(DateTimeFormatter.ISO_DATE))
                        .param("username", checkUser.getUsername())
                        .param("password", checkUser.getPassword()))
                .andExpect(redirectedUrl("/user/profile/"+checkUser.getUsername()));
        var argumentCaptor =
                forClass(NewsUser.class);
        //verify(newsUserRepository).save(argumentCaptor.capture());
        var savedUser = argumentCaptor.getValue();
        assertThat(checkUser).usingRecursiveComparison()
                .ignoringFields("password").isEqualTo(savedUser);
        assertThat(passwordEncoder.matches(
                checkUser.getPassword(), savedUser.getPassword())).isTrue();
    }
    @Test
    void testInvalidRegistrationFormFails() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/register").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstname", "first")
                        .param("lastname", "last")
                        .param("birthday", "1970-01-01")
                        .param("username", "use")
                        .param("password", "pass"))
                .andExpect(view().name("register"))
                .andExpect(content().string(
                        containsString("Muss mind. 4 Zeichen lang sein.")));
        verify(newsUserRepository, never()).save(any());
    }
    @Test
    void testRegistrationFormFailsWhenUserExists() throws Exception {
        when(newsUserRepository.findByUsername("user")).thenReturn(new de.sme.entity.NewsUser());
        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/register").with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstname", "first")
                        .param("lastname", "last")
                        .param("birthday", "1970-01-01")
                        .param("username", "user")
                        .param("password", "pass"))
                .andExpect(view().name("register"))
                .andExpect(content().string(
                        containsString("Benutzer existiert bereits.")));
        verify(newsUserRepository, never()).save(any());
    }



}
