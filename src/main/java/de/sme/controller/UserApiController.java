package de.sme.controller;

import de.sme.entity.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    NewsUserRepository newsUserRepository;

    public UserApiController(NewsUserRepository newsUserRepository) {
        this.newsUserRepository = newsUserRepository;
    }
    @GetMapping
    public List<NewsUser> findAll() {
        return newsUserRepository.findAll();
    }
}

