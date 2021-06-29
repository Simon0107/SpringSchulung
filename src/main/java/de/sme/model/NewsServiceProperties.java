package de.sme.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "newsapi")
public class NewsServiceProperties {
    private String baseUrl;
    private String key;
}
