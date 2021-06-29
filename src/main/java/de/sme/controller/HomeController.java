package de.sme.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/") // shortcut
    //@RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "home";
    }

}
