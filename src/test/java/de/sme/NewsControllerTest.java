package de.sme;

import de.sme.controller.NewsController;
import de.sme.model.NewsItem;
import de.sme.service.NewsService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NewsController.class)
public class NewsControllerTest {

    @MockBean
    NewsService newsService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testNewsControllerWithParameter() throws Exception {
        var mockItems = new ArrayList<NewsItem>();
        for (int i = 0; i < 12; i++) {
            mockItems.add(new NewsItem("Title " + i, "Desc", "", ""));
        }
        when(newsService.findNews(12)).thenReturn(mockItems);
        mockMvc.perform(MockMvcRequestBuilders.get("/news?count=12"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("news",
                        hasItems(mockItems.toArray())))
                .andExpect(content().string(
                        Matchers.containsString("Title 0")));
    }
}
