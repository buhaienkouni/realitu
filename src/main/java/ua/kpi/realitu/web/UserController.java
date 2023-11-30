package ua.kpi.realitu.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.service.UserService;
import ua.kpi.realitu.web.model.NewUserDto;
import ua.kpi.realitu.web.model.UserDto;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    private void copywritersAndCurrentUser(Model model, Authentication authentication) {
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());
        List<UserDto> copywriterDtoList = userService.getAllCopywriters();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("copywriters", copywriterDtoList);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String users(Model model, Authentication authentication) {
        model.addAttribute("newUserDto", new NewUserDto());
        copywritersAndCurrentUser(model, authentication);

        return "users";
    }

    @PostMapping("/users/new")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String newUser(@Valid @ModelAttribute("newUserDto") NewUserDto newUserDto,
                          BindingResult bindingResult, Model model, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("newUserDto", newUserDto);
            copywritersAndCurrentUser(model, authentication);

            return "users";
        }

        try {
            userService.createUser(newUserDto);

        } catch (RuntimeException e) {
            bindingResult.rejectValue("username", "usernameNotUnique", e.getMessage());
            model.addAttribute("newUserDto", newUserDto);
            copywritersAndCurrentUser(model, authentication);

            return "users";
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/delete")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String deleteUser(@PathVariable("id") UUID id) {

        userService.deleteUser(id);
        return "redirect:/users";
    }

    @PostMapping("/users/update")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String updateUser(@Valid @ModelAttribute("userDto") UserDto userDto,
                             BindingResult bindingResult, Model model, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            model.addAttribute("newUserDto", new NewUserDto());
            copywritersAndCurrentUser(model, authentication);
            return "users";
        }
        userService.updateUser(userDto);

        return "redirect:/users";
    }
}
