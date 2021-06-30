package de.sme;

import de.sme.controller.RegistrationController;
import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {

    @MockBean
    NewsUserRepository newsUserRepository;
    @Autowired
    private MockMvc mockMvc;
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
        var checkUser = new NewsUser("user", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        mockMvc
                .perform(MockMvcRequestBuilders.post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("firstname", checkUser.getFirstname())
                        .param("lastname", checkUser.getLastname())
                        .param("birthday",
                                checkUser.getBirthday().format(DateTimeFormatter.ISO_DATE))
                        .param("username", checkUser.getUsername())
                        .param("password", checkUser.getPassword()))
                .andExpect(redirectedUrl("/user/profile/"+checkUser.getUsername()));
        verify(newsUserRepository).save(checkUser);
    }
}
