package de.sme;

import de.sme.entity.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("inmemory")
class UserApiIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    NewsUserRepository newsUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @AfterEach
    void cleanup() {
        newsUserRepository.deleteById("user");
    }
    @Test
    void testSave() {
        var testUser = new NewsUser("user", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        var uri = restTemplate.postForLocation("/api/user", testUser);
        var userInDb = newsUserRepository.findByUsername(testUser.getUsername());
        assertThat(testUser).usingRecursiveComparison()
                .ignoringFields("password", "version").isEqualTo(userInDb);
        assertThat(passwordEncoder.matches(
                testUser.getPassword(), userInDb.getPassword())).isTrue();
    }


}

