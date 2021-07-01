package de.sme;

import de.sme.controller.UserProfileController;
import de.sme.entity.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    NewsUserRepository newsUserRepository;

    @Test
    void testProfile() throws Exception {
        NewsUser testuser = new NewsUser("user", "pass",
                "first", "last", LocalDate.now());
        when(newsUserRepository.findByUsername(testuser.getUsername()))
                .thenReturn(testuser);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/profile/{username}", testuser.getUsername()))
                .andExpect(model().attributeExists("newsUser"))
                .andExpect(content().string(
                        containsString("Hello " + testuser.getFirstname())));
        verify(newsUserRepository).findByUsername(testuser.getUsername());
    }

    @Test
    void testNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/profile/{username}", "unknown"))
                .andExpect(view().name("usernotfound"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        containsString("We can not find this user.")));
    }
}
