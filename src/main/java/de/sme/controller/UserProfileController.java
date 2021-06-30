package de.sme.controller;

import de.sme.repo.NewsUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

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
        if (user == null) {
            throw new UnknownNewsUserException("User not found");
        }
        model.addAttribute("newsUser", user);
        return "profile";
    }

    @ExceptionHandler(UnknownNewsUserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String userNotFound() {
        return "usernotfound";
    }


}

