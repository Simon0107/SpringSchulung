package de.sme;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;


@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("inmemory")
class RegistrationIntegrationTest {
    @TestConfiguration
    static class MockRepositoryConfiguration {
        @Primary
        @Bean
        NewsUserRepository newsUserRepositoryMock(NewsUserRepository real) {
            return Mockito.mock(NewsUserRepository.class,
                    MockReset.withSettings(MockReset.AFTER)
                            .defaultAnswer(AdditionalAnswers.delegatesTo(real)));
        }
    }
    @LocalServerPort
    int port;
    @SpyBean
    NewsUserRepository newsUserRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void testRegistration() {
        var checkUser = new NewsUser("user", "pass",
                "first", "last", LocalDate.of(1970, 1, 1));
        try (var playwright = Playwright.create()) {
            var browserType = playwright.chromium();
            var launchOptions = new BrowserType.LaunchOptions();
            launchOptions.headless = true; // set this to false
            try (var browser = browserType.launch(launchOptions)) {
                var contextOptions = new Browser.NewContextOptions();
                contextOptions.locale="de-DE";
                var context = browser.newContext(contextOptions);
                var page = context.newPage();
                page.navigate("http://localhost:" + port + "/user/register");
                page.type("#firstname", checkUser.getFirstname());
                page.type("#lastname", checkUser.getLastname());
                page.type("#username", checkUser.getUsername());
                page.type("#password", checkUser.getPassword());
                page.type("#birthday", checkUser.getBirthday()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                page.click("button");

                var argumentCaptor =
                        ArgumentCaptor.forClass(de.sme.entity.NewsUser.class);
                verify(newsUserRepository).save(argumentCaptor.capture());
                var savedUser = argumentCaptor.getValue();
                assertThat(checkUser).usingRecursiveComparison()
                        .ignoringFields("password", "version").isEqualTo(savedUser);
                assertThat(passwordEncoder.matches(
                        checkUser.getPassword(), savedUser.getPassword())).isTrue();
            }
        }
    }


}

