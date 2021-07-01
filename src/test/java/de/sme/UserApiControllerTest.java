package de.sme;

import de.sme.controller.UserApiController;
import de.sme.entity.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserApiController.class)
class UserApiControllerTest {
    @Autowired
    MockMvc mockMvc;
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
                        .value(testUser.getPassword()));
        verify(newsUserRepository).findAll();
    }

}
