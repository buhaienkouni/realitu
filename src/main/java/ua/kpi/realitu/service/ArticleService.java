package ua.kpi.realitu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.domain.enums.Category;
import ua.kpi.realitu.repository.ArticleRepository;
import ua.kpi.realitu.repository.ImageRepository;
import ua.kpi.realitu.repository.UserRepository;
import ua.kpi.realitu.service.converter.ArticleDtoToEntityConverter;
import ua.kpi.realitu.service.converter.ArticleEntityToDtoConverter;
import ua.kpi.realitu.web.model.ArticleDto;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleService {

    @Autowired
    private UserRepository userRepository;

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
        if (articleExists(articleDto.getTitle())) {
            throw new RuntimeException("Article with this title already exists");
        } else {
            Article article = articleDtoToEntityConverter.create(articleDto, principalUser);
            articleRepository.save(article);

            imageService.uploadImageAndSaveToArticle(articleDto.getImage(), article.getId());
        }
    }

    public void updateArticle(ArticleDto articleDto) throws IOException {
        if (articleExists(articleDto.getTitle())) {
            throw new RuntimeException("Article with this title already exists");
        } else {
            Article article = getArticleById(articleDto.getId());
            articleDtoToEntityConverter.update(articleDto, article);
            articleRepository.save(article);

            imageService.uploadImageAndSaveToArticle(articleDto.getImage(), article.getId());
        }
    }

    public ArticleDto getArticleDtoById(UUID id) {
        Article article = getArticleById(id);

        return articleEntityToDtoConverter.convert(article);
    }

    public List<ArticleDto> getAllArticleDtoList() {
        return articleRepository.findAll().stream()
                .sorted(Comparator.comparing(Article::getCreationDate).reversed())
                .map(articleEntityToDtoConverter::convert)
                .toList();
    }

    public ArticleDto getLatestArticle() {
        return articleRepository.findFirstByOrderByCreationDateDesc()
                .map(articleEntityToDtoConverter::convert)
                .orElse(null);
    }


    public List<ArticleDto> getArticleDtoListByCategory(Category category) {
        return articleRepository.findAllByCategory(category).stream()
                .sorted(Comparator.comparing(Article::getCreationDate).reversed())
                .map(articleEntityToDtoConverter::convert)
                .toList();
    }

    // Change order in sql query
    // Add minus latest where needed
    public List<ArticleDto> getArticleDtoListByUser(UserEntity principalUser) {
        if (principalUser.getRole() == Role.COPYWRITER) {
            return articleRepository.findAllByAuthorId(principalUser.getId()).stream()
                    .sorted(Comparator.comparing(Article::getCreationDate).reversed())
                    .map(articleEntityToDtoConverter::convert)
                    .toList();
        } else if (principalUser.getRole() == Role.SUPER_ADMIN) {
            return articleRepository.findAll().stream()
                    .sorted(Comparator.comparing(Article::getCreationDate).reversed())
                    .map(articleEntityToDtoConverter::convert)
                    .toList();
        }
        return null;
    }

    public void deleteArticleByIdAndItsImage(UUID id, UserEntity principalUser) {
        Article article = getArticleById(id);

        if (article.getAuthor().getId() == principalUser.getId() || principalUser.getRole() == Role.SUPER_ADMIN) {
            Optional.ofNullable(article.getImage())
                    .ifPresent(imageRepository::delete);
            articleRepository.delete(article);
        } else {
            throw new RuntimeException("You are not allowed to delete this article");
        }
    }

    public Boolean articleExists(String title) {
        return articleRepository.findByTitle(title.strip()).isPresent();
    }

    public Article getArticleById(UUID id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Could not find article with id %s.".formatted(id)));
    }
}
