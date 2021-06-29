package de.sme.controller;

import de.sme.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NewsController {
    private NewsService newsService;
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public String showRecentNews(Model model,
                                 @RequestParam(name = "count", defaultValue = "5") int count) {
        var news = newsService.findNews(count);
        model.addAttribute("news", news);
        return "news";
    }
}
