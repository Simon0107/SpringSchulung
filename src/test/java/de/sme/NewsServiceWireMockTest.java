package de.sme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import de.sme.container.NewsItemContainer;
import de.sme.controller.NewsController;
import de.sme.model.NewsItem;
import de.sme.model.NewsServiceProperties;
import de.sme.service.NewsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@Import({
        NewsService.class,
        RestTemplate.class,
        JacksonAutoConfiguration.class
})
@TestPropertySource(properties = {
        "newsapi.key=testkey",
        "newsapi.baseurl=http://localhost:8090/"
})
@EnableConfigurationProperties(NewsServiceProperties.class)
@WebMvcTest(NewsController.class)
public class NewsServiceWireMockTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private MockMvc mockMvc;

    NewsItemContainer mockNewsItemContainer = new NewsItemContainer(List.of(
            new NewsItem("Title", "Description", "Url", "Image")));

    WireMockServer wireMockServer;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
        ObjectMapper mapper = new ObjectMapper();
        wireMockServer.stubFor(get(urlPathEqualTo("/v2/top-headlines"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withJsonBody(mapper.valueToTree(mockNewsItemContainer))));
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }
        @Autowired
        NewsService newsService;

        @Test
        void testNewsService() {
            var newsItems = newsService.findNews(1);
            assertEquals(mockNewsItemContainer.getArticles(), newsItems);
            wireMockServer.verify(getRequestedFor(urlPathEqualTo("/v2/top-headlines"))
                    .withQueryParam("apiKey", equalTo("testkey"))
                    .withQueryParam("pageSize", equalTo("1"))
                    .withQueryParam("country", equalTo("de"))
                    .withQueryParam("page", equalTo("0")));
        }

}
