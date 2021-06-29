package de.sme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsItem {
    private String title;
    private String description;
    private String url;
    private String urlToImage;
}
