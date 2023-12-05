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

        ArticleDto latestWarArticle = articleService.getLatestArticleDtoNotHistories(Category.WAR);
        List<ArticleDto> warArticles3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListByCategory(Category.WAR), List.of(latestWarArticle), 3);

        ArticleDto latestPoliticArticle = articleService.getLatestArticleDtoNotHistories(Category.POLITIC);
        List<ArticleDto> politicArticles3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListByCategory(Category.POLITIC), List.of(latestWarArticle), 3);

        List<ArticleDto> histories5 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListByCategory(Category.HISTORY), null, 5);
        List<ArticleDto> histories3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListByCategory(Category.HISTORY), null, 3);

        currentUser(model, authentication);
        model.addAttribute("latestWarArticle", latestWarArticle);
        model.addAttribute("warArticles3", warArticles3);
        model.addAttribute("latestPoliticArticle", latestPoliticArticle);
        model.addAttribute("politicArticles3", politicArticles3);
        model.addAttribute("histories", histories5);
        model.addAttribute("histories3", histories3);

        return "home";
    }

    @GetMapping("/home/{category}")
    public String articlesByCategory(@PathVariable("category") String category, Model model, Authentication authentication) {
        List<ArticleDto> articles = List.of();
        if (category.equals("war")) {
            articles = articleService.getArticleDtoListByCategory(Category.WAR);
        } else if (category.equals("politic")) {
            articles = articleService.getArticleDtoListByCategory(Category.POLITIC);
        }

        currentUser(model, authentication);
        model.addAttribute("articles", articles);
        model.addAttribute("articlesAreEmpty", articles.isEmpty());

        return "oneCategoryArticles";
    }

    @GetMapping("/histories")
    public String articlesHistories(Model model, Authentication authentication) {
        List<ArticleDto> articles = articleService.getArticleDtoListByCategory(Category.HISTORY);

        currentUser(model, authentication);
        model.addAttribute("articles", articles);
        model.addAttribute("articlesAreEmpty", articles.isEmpty());

        return "onlyHistoryArticles";
    }

    @GetMapping("/home/article/{title}/{id}")
    public String currentArticle(@PathVariable("id") UUID id, Model model, Authentication authentication) {
        ArticleDto thisArticle = articleService.getArticleDtoById(id);
        List<ArticleDto> histories5 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListByCategory(Category.HISTORY), null, 5);

        List<ArticleDto> histories3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListByCategory(Category.HISTORY), null, 3);

        List<ArticleDto> recommendations3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListButNotCurrentAndNotHistories(thisArticle), null, 3);

        currentUser(model, authentication);
        model.addAttribute("article", thisArticle);
        model.addAttribute("histories", histories5);
        model.addAttribute("histories3", histories3);
        model.addAttribute("recommendations", recommendations3);

        return "article";
    }

    @GetMapping("/histories/article/{title}/{id}")
    public String currentArticleHistory(@PathVariable("id") UUID id, Model model, Authentication authentication) {
        ArticleDto thisHistoryArticle = articleService.getArticleDtoById(id);
        List<ArticleDto> histories5 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getHistoryArticleDtoListButNotCurrent(thisHistoryArticle), null, 5);

        List<ArticleDto> histories3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getHistoryArticleDtoListButNotCurrent(thisHistoryArticle), null, 5);

        List<ArticleDto> recommendations3 = articleService.getSomeAmountOfArticlesDtoListAndRemoveUsedArticles(
                articleService.getArticleDtoListButNotCurrentAndNotHistories(thisHistoryArticle), null, 3);

        currentUser(model, authentication);
        model.addAttribute("article", thisHistoryArticle);
        model.addAttribute("histories", histories5);
        model.addAttribute("histories3", histories3);
        model.addAttribute("recommendations", recommendations3);

        return "historyArticle";
    }

    @GetMapping({"/image/{articleId}", "/histories/article/{title}/image/{articleId}", "/home/image/{articleId}", "/home/article/{title}/image/{articleId}"})
    public ResponseEntity<byte[]> getImage(@PathVariable("articleId") UUID articleId) {
        try {
            return ResponseEntity.ok().body(imageService.getArticleImageByArticleId(articleId));

        } catch (RuntimeException e) {
            throw new RuntimeException(HttpStatus.NOT_FOUND.getReasonPhrase(), e);
        }
    }

    @GetMapping("/articles")
    @PreAuthorize("hasAuthority('WRITE_ARTICLE') or hasAuthority('WRITE_MY_ARTICLE')")
    public String currentUserArticles(Model model, Authentication authentication) {
        UserEntity currentUser = userService.getUserByUsername(authentication.getName());
        List<ArticleDto> thisUserArticleDtoList = articleService.getArticleDtoListByUser(currentUser);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("articles", thisUserArticleDtoList);
        model.addAttribute("articlesListIsEmpty", thisUserArticleDtoList.isEmpty());
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
            model.addAttribute("articlesListIsEmpty", thisUserArticleDtoList.isEmpty());

            return "userArticles";
        }

        try {
            articleService.createArticle(articleDto, currentUser);

        } catch (RuntimeException e) {
            bindingResult.rejectValue("title", "titleNotUnique", e.getMessage());

            model.addAttribute("articleDto", articleDto);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("articles", thisUserArticleDtoList);
            model.addAttribute("articlesListIsEmpty", thisUserArticleDtoList.isEmpty());

            return "userArticles";

        } catch (IOException e) {
            bindingResult.rejectValue("image", "imageDoesNotFit", "Image does not fit");

            model.addAttribute("article", articleDto);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("articles", thisUserArticleDtoList);
            model.addAttribute("articlesListIsEmpty", thisUserArticleDtoList.isEmpty());

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
        ArticleDto thisArticle = articleService.getArticleDtoByIdForEditing(id);

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
}
