package de.sme.controller;

import de.sme.repo.NewsUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserProfileController {
    NewsUserRepository newsUserRepository;

    public UserProfileController(NewsUserRepository newsUserRepository) {
        this.newsUserRepository = newsUserRepository;
    }
    @GetMapping("/user/profile/{username}")
    public String showProfile(Model model,
                              @PathVariable("username") String username) {
        var user = newsUserRepository.findByUsername(username);
        model.addAttribute("newsUser", user);
        return "profile";
    }
}
