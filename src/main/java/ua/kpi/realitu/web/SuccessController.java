package ua.kpi.realitu.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.kpi.realitu.service.SuggestService;
import ua.kpi.realitu.service.UserService;
import ua.kpi.realitu.web.model.StoryDto;

import java.util.Optional;

@Controller
public class SuccessController {

    @Autowired
    private SuggestService suggestService;

    @Autowired
    private UserService userService;

    private void currentUser(Model model, Authentication authentication) {
        Optional.ofNullable(authentication).ifPresentOrElse(
                auth -> model.addAttribute("currentUser", userService.getUserByUsername(authentication.getName())),
                () -> model.addAttribute("currentUser", null)
        );
    }

    @GetMapping("/success")
    public String successPage(Model model, Authentication authentication) {
        currentUser(model, authentication);

        return "success";
    }
}
