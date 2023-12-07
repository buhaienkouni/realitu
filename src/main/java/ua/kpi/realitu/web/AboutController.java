package ua.kpi.realitu.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.kpi.realitu.service.ArticleService;
import ua.kpi.realitu.service.ImageService;
import ua.kpi.realitu.service.UserService;

import java.util.Optional;

@Controller
public class AboutController {

    @Autowired
    private UserService userService;

    @GetMapping("/about")
    public String homeWithArticles(Model model, Authentication authentication) {

        Optional.ofNullable(authentication).ifPresentOrElse(
                auth -> model.addAttribute("currentUser", userService.getUserByUsername(authentication.getName())),
                () -> model.addAttribute("currentUser", null)
        );
        return "about";
    }
}
