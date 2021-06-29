package de.sme.service;

import de.sme.model.NewsItem;

import java.util.List;
public interface NewsService {
    List<NewsItem> findNews(int count);
}
