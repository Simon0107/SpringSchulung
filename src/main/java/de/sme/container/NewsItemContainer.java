package de.sme.container;

import de.sme.model.NewsItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsItemContainer {
    private List<NewsItem> articles = new ArrayList<>();
}

