package de.sme.controller;

import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/register")
public class RegistrationController {

    NewsUserRepository newsUserRepository;

    public RegistrationController(NewsUserRepository newsUserRepository) {
        this.newsUserRepository = newsUserRepository;
    }

    @GetMapping
    public String showRegistration(Model model) {
        model.addAttribute("newsUser", new NewsUser());
        return "register";
    }

    @PostMapping
    public String processRegistration(NewsUser user,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        newsUserRepository.save(user);
        return "redirect:/user/profile/" + user.getUsername();
    }
}
