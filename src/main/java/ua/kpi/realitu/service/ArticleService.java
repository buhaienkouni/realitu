package ua.kpi.realitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.Image;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.domain.enums.Category;
import ua.kpi.realitu.repository.ArticleRepository;
import ua.kpi.realitu.repository.ImageRepository;
import ua.kpi.realitu.service.converter.ArticleDtoToEntityConverter;
import ua.kpi.realitu.service.converter.ArticleEntityToDtoConverter;
import ua.kpi.realitu.web.model.ArticleDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ArticleDtoToEntityConverter articleDtoToEntityConverter;

    @Autowired
    private ArticleEntityToDtoConverter articleEntityToDtoConverter;

    public void createArticle(ArticleDto articleDto, UserEntity principalUser) throws IOException {
        if (articleRepository.findByTitle(articleDto.getTitle().strip().trim()).isPresent()) {
            throw new RuntimeException("Article with this title already exists");
        } else {
            Article article = articleDtoToEntityConverter.create(articleDto, principalUser);
            articleRepository.save(article);

            imageService.uploadImageAndSaveToArticle(articleDto.getImage(), article.getId());
        }
    }

    public void updateArticle(ArticleDto articleDto, UserEntity principalUser) throws IOException {
        if (articleRepository.findByTitle(articleDto.getTitle().strip().trim()).isPresent()
                && !articleRepository.findByTitle(articleDto.getTitle().strip().trim()).get().getId().equals(articleDto.getId())) {
            throw new RuntimeException("Article with this title already exists");
        } else {
            Article article = getArticleById(articleDto.getId());

            if (article.getAuthor().getId() == principalUser.getId() || principalUser.getRole() == Role.SUPER_ADMIN) {
                articleDtoToEntityConverter.update(articleDto, article);
                articleRepository.save(article);

                imageService.uploadImageAndSaveToArticle(articleDto.getImage(), article.getId());
            } else {
                throw new RuntimeException("You are not allowed to delete this article");
            }
        }
    }

    public List<ArticleDto> getSomeAmountOfArticlesDtoListAndRemoveUsedArticle(
            List<ArticleDto> listToSort, ArticleDto usedArticle, int amount) {

        List<ArticleDto> mutableListToSort = new ArrayList<>(listToSort);

        if (usedArticle != null) {
            mutableListToSort.removeIf(article -> article.equals(usedArticle));
        }

        if (mutableListToSort.size() <= amount) {
            return mutableListToSort;
        }

        return mutableListToSort.stream()
                .limit(amount)
                .toList();
    }

    public ArticleDto getArticleDtoById(UUID id) {
        Article article = getArticleById(id);

        return articleEntityToDtoConverter.convert(article);
    }

    public ArticleDto getArticleDtoByIdForEditing(UUID id) {
        Article article = getArticleById(id);

        return articleEntityToDtoConverter.convertForEditing(article);
    }

    public List<ArticleDto> getAllArticleDtoList() {
        return articleRepository.findAllByOrderByCreationDateDesc().stream()
                .map(articleEntityToDtoConverter::convert)
                .toList();
    }

    public ArticleDto getLatestArticleDtoNotHistories(Category category) {
        return articleRepository.findAllByOrderByCreationDateDesc().stream()
                .filter(article -> article.getCategory() != Category.HISTORY)
                .filter(article -> article.getCategory() == category)
                .findFirst()
                .map(articleEntityToDtoConverter::convert)
                .orElse(null);
    }

    public List<ArticleDto> getArticleDtoListByCategory(Category category) {
        if (category == Category.HISTORY) {
            return Optional.ofNullable(articleRepository.findAllByCategoryOrderByCreationDateDesc(category))
                    .map(articles -> articles.stream()
                            .map(articleEntityToDtoConverter::convert)
                            .toList())
                    .orElseGet(List::of);
        } else {
            return Optional.ofNullable(getLatestArticleDtoNotHistories(category))
                    .map(latestArticle -> articleRepository.findAllByCategoryOrderByCreationDateDesc(category).stream()
                            .filter(article -> !article.getId().equals(latestArticle.getId()))
                            .map(articleEntityToDtoConverter::convert)
                            .toList())
                    .orElseGet(List::of);
        }
    }

    public List<ArticleDto> getArticleDtoListButNotCurrentAndNotHistories(ArticleDto articleDto) {
        return articleRepository.findAllByOrderByCreationDateDesc().stream()
                        .filter(article -> !article.getId().equals(articleDto.getId()))
                        .filter(article -> !article.getCategory().equals(Category.HISTORY))
                        .map(articleEntityToDtoConverter::convert)
                        .toList();
    }

    public List<ArticleDto> getHistoryArticleDtoListButNotCurrent(ArticleDto articleDto) {
        return articleRepository.findAllByCategoryOrderByCreationDateDesc(Category.HISTORY).stream()
                .filter(article -> !article.getId().equals(articleDto.getId()))
                .map(articleEntityToDtoConverter::convert)
                .toList();
    }

    public List<ArticleDto> getArticleDtoListByUser(UserEntity principalUser) {
        if (principalUser.getRole() == Role.COPYWRITER) {
            return articleRepository.findAllByAuthorIdOrderByCreationDateDesc(principalUser.getId()).stream()
                    .map(articleEntityToDtoConverter::convert)
                    .toList();
        } else if (principalUser.getRole() == Role.SUPER_ADMIN) {
            return articleRepository.findAllByOrderByCreationDateDesc().stream()
                    .map(articleEntityToDtoConverter::convert)
                    .toList();
        }
        return null;
    }

    public void deleteArticleByIdAndItsImage(UUID id, UserEntity principalUser) {
        Article article = getArticleById(id);

        if (article.getAuthor().getId() == principalUser.getId() || principalUser.getRole() == Role.SUPER_ADMIN) {
            Image image = article.getImage();
            articleRepository.delete(article);

            if (image != null) {
                imageRepository.delete(image);
            }
        } else {
            throw new RuntimeException("You are not allowed to delete this article");
        }
    }

    private Article getArticleById(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find article with id %s.".formatted(id)));
    }
}
