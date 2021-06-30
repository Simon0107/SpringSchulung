package de.sme;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;

import javax.management.Query;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegistrationIntegrationTest {
    @LocalServerPort
    int port;
    @SpyBean
    NewsUserRepository newsUserRepository;

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
                verify(newsUserRepository, timeout(500).times(1)).save(checkUser);
                assertEquals("http://localhost:" + port +
                        "/user/profile/"+checkUser.getUsername(),page.url());
            }
        }
    }


}

