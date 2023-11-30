package ua.kpi.realitu.web;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import ua.kpi.realitu.domain.enums.Category;
import ua.kpi.realitu.service.ArticleService;
import ua.kpi.realitu.service.ImageService;
import ua.kpi.realitu.service.UserService;
import ua.kpi.realitu.web.model.ArticleDto;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ArticleController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ImageService imageService;

    private void currentUser(Model model, Authentication authentication) {
        Optional.ofNullable(authentication).ifPresentOrElse(
                auth -> model.addAttribute("currentUser", userService.getUserByUsername(authentication.getName())),
                () -> model.addAttribute("currentUser", null)
        );
    }

    @GetMapping("/home")
    public String homeWithArticles(Model model, Authentication authentication) {
        ArticleDto latestArticle = articleService.getLatestArticleDto();
        List<ArticleDto> someArticles = articleService.getArticleDtoListByCategory(Category.WAR);

        currentUser(model, authentication);
        model.addAttribute("latestArticle", latestArticle);
        model.addAttribute("someArticles", someArticles);

        return "home";
    }

    @GetMapping("/article/{id}/{title}")
    public String currentArticle(@PathVariable("id") UUID id, Model model, Authentication authentication) {
        ArticleDto thisArticle = articleService.getArticleDtoById(id);

        currentUser(model, authentication);
        model.addAttribute("article", thisArticle);

        return "article";
    }

    @GetMapping("/articles")
    @PreAuthorize("hasAuthority('WRITE_ARTICLE') or hasAuthority('WRITE_MY_ARTICLE')")
    public String currentUserArticles(Model model, Authentication authentication) {
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());
        List<ArticleDto> thisUserArticleDtoList = articleService.getArticleDtoListByUser(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("articles", thisUserArticleDtoList);
        model.addAttribute("article", new ArticleDto());

        return "userArticles";
    }

    @PostMapping("/articles/new")
    @PreAuthorize("hasAuthority('WRITE_ARTICLE') or hasAuthority('WRITE_MY_ARTICLE')")
    public String newArticle(@Valid @ModelAttribute("article") ArticleDto articleDto,
                             BindingResult bindingResult, Model model, Authentication authentication) {

        UserEntity currentUser = userService.getUserByUsername(authentication.getName());
        List<ArticleDto> thisUserArticleDtoList = articleService.getArticleDtoListByUser(currentUser);

        if (bindingResult.hasErrors()) {
            model.addAttribute("articleDto", articleDto);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("articles", thisUserArticleDtoList);

            return "userArticles";
        }

        try {
            articleService.createArticle(articleDto, currentUser);

        } catch (RuntimeException e) {
            bindingResult.rejectValue("title", "titleNotUnique", e.getMessage());

            model.addAttribute("articleDto", articleDto);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("articles", thisUserArticleDtoList);

            return "userArticles";

        } catch (IOException e) {
            bindingResult.rejectValue("image", "imageDoesNotFit", "Image does not fit");

            model.addAttribute("article", articleDto);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("articles", thisUserArticleDtoList);

            return "userArticles";
        }
        return "redirect:/articles";
    }

    @PostMapping("/articles/{id}/delete")
    @PreAuthorize("hasAuthority('WRITE_ARTICLE') or hasAuthority('WRITE_MY_ARTICLE')")
    public String deleteArticle(@PathVariable("id") UUID id, Authentication authentication) {
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());

        articleService.deleteArticleByIdAndItsImage(id, currentUser);
        return "redirect:/articles";
    }

    @GetMapping("/articles/{id}")
    @PreAuthorize("hasAuthority('WRITE_ARTICLE') or hasAuthority('WRITE_MY_ARTICLE')")
    public String currentArticleToChange(@PathVariable("id") UUID id, Model model, Authentication authentication) {
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());
        ArticleDto thisArticle = articleService.getArticleDtoById(id);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("article", thisArticle);

        return "editArticle";
    }

    @PostMapping("/articles/update")
    @PreAuthorize("hasAuthority('WRITE_ARTICLE') or hasAuthority('WRITE_MY_ARTICLE')")
    public String updateArticle(@Valid @ModelAttribute("article") ArticleDto articleDto,
                             BindingResult bindingResult, Model model, Authentication authentication) {

        UserEntity currentUser = userService.getUserByUsername(authentication.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("article", articleDto);
            model.addAttribute("currentUser", currentUser);

            return "editArticle";
        }

        try {
            articleService.updateArticle(articleDto, currentUser);

        } catch (RuntimeException e) {
            bindingResult.rejectValue("title", "titleNotUnique", e.getMessage());

            model.addAttribute("article", articleDto);
            model.addAttribute("currentUser", currentUser);

            return "editArticle";

        } catch (IOException e) {
            bindingResult.rejectValue("image", "imageDoesNotFit", "Image does not fit");

            model.addAttribute("article", articleDto);
            model.addAttribute("currentUser", currentUser);

            return "editArticle";
        }
        return "redirect:/articles";
    }

    @GetMapping("/image/{articleId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("articleId") UUID articleId) {
        try {
            return ResponseEntity.ok().body(imageService.getArticleImageByArticleId(articleId));

        } catch (RuntimeException e) {
            throw new RuntimeException(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
        }
    }
}
