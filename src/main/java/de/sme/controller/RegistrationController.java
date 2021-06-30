package de.sme.controller;

import de.sme.model.NewsUser;
import de.sme.repo.NewsUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/user/register")
public class RegistrationController {

    NewsUserRepository newsUserRepository;
    PasswordEncoder passwordEncoder;

    public RegistrationController(NewsUserRepository newsUserRepository, PasswordEncoder passwordEncoder) {
        this.newsUserRepository = newsUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showRegistration(Model model) {
        model.addAttribute("newsUser", new NewsUser());
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid NewsUser user,
                                      BindingResult bindingResult) {
        if (newsUserRepository.findByUsername(user.getUsername()) != null) {
            bindingResult.addError(new FieldError("newsUser",
                    "username",
                    "Benutzer existiert bereits."));
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        var passwordHash = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordHash);
        newsUserRepository.save(user);
        return "redirect:/user/profile/" + user.getUsername();
    }

}
