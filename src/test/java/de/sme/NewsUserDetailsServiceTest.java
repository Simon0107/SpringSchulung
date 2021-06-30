package de.sme;

import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@SpringBootTest
@AutoConfigureMockMvc
public class NewsUserDetailsServiceTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @MockBean
    NewsUserRepository newsUserRepository;

    @Test
    void testLoginSuccess() throws Exception {
        var testUser =
                new NewsUser("testuser", passwordEncoder.encode("pass"),
                        "first", "last", LocalDate.now());
        Mockito.when(newsUserRepository.findByUsername("testuser")).thenReturn(testUser);
        mockMvc.perform(
                formLogin().user("testuser").password("pass"))
                .andExpect(authenticated());
    }
    @Test
    void testLoginFail() throws Exception {
        Mockito.when(newsUserRepository.findByUsername("testuser")).thenReturn(null);
        mockMvc.perform(
                formLogin().user("testuser").password("wrong"))
                .andExpect(unauthenticated());
    }

}

