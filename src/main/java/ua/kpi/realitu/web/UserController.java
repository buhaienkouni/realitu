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
import ua.kpi.realitu.service.converter.UserEntityToDtoConverter;
import ua.kpi.realitu.web.model.NewUserDto;
import ua.kpi.realitu.web.model.UserDto;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserEntityToDtoConverter userEntityToDtoConverter;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    private void usersModel(Model model, Authentication authentication) {
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());
        List<UserDto.ReadDto> copywriterDtoList = userService.getAllCopywriters();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("copywriters", copywriterDtoList);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String users(Model model, Authentication authentication) {
        model.addAttribute("userDto", new NewUserDto());
        usersModel(model, authentication);

        return "users";
    }

    @PostMapping("/users/new")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String newUser(@Valid @ModelAttribute("userDto") NewUserDto userDto, BindingResult bindingResult, Model model, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            usersModel(model, authentication);

            return "users";
        }

        try {
            userService.createUser(userDto);

        } catch (RuntimeException e) {
            bindingResult.rejectValue("username", "usernameNotUnique", "User with username %s already exists".formatted(userDto.getUsername()));
            model.addAttribute("userDto", userDto);
            usersModel(model, authentication);

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

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String userToEdit(@PathVariable("id") UUID id, Model model, Authentication authentication) {
        UserDto.ReadDto userDto = userEntityToDtoConverter.convert(userService.getUserById(id));
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userDto", userDto);

        return "newUser";
    }

    @PostMapping("/users/{id}/update")
    @PreAuthorize("hasAuthority('WRITE_USER')")
    public String updateUser(@Valid @ModelAttribute("userDto") UserDto userDto, BindingResult bindingResult, Model model, Authentication authentication) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userDto", userDto);
            return "newUser";
        }
        userService.updateUser(userDto);

        return "redirect:/users";
    }
}
