package de.sme.service;

import de.sme.container.NewsItemContainer;
import de.sme.model.NewsItem;
import de.sme.model.NewsServiceProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class NewsServiceImpl implements NewsService {
    private NewsServiceProperties properties;
    private RestTemplate restTemplate;

    public NewsServiceImpl(NewsServiceProperties properties,
                           RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    private static final String api = "v2/top-headlines?" +
            "country=de&" +
            "apiKey={apikey}&" +
            "pageSize={count}&" +
            "page=0";

    @Override
    @Cacheable(cacheNames = "newscache")
    public List<NewsItem> findNews(int count) {
        var params = Map.of(
                "count", String.valueOf(count),
                "apikey", properties.getKey()
        );
        var container =
                restTemplate.getForObject(
                        properties.getBaseUrl() + api,
                        NewsItemContainer.class,
                        params);
        return container.getArticles();
    }
}