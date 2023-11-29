package ua.kpi.realitu.service.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.repository.UserRepository;
import ua.kpi.realitu.web.model.ArticleDto;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ArticleEntityToDtoConverter {

    @Autowired
    private UserRepository userRepository;

    public ArticleDto convert(Article articleEntity) {
        ArticleDto articleDto = new ArticleDto();

        articleDto.setId(articleEntity.getId());
        articleDto.setTitle(articleEntity.getTitle());
        articleDto.setContent(articleEntity.getContent());
        articleDto.setCreationDate(articleEntity.getCreationDate());
        articleDto.setCategory(articleEntity.getCategory());
        Optional.ofNullable(articleEntity.getImage())
                .ifPresent(image -> {
                    articleDto.setImageId(image.getId());
                    articleDto.setImageTitle(articleEntity.getImageTitle());
                });
        articleDto.setAuthorName(articleEntity.getAuthor().getUsername());

        return articleDto;
    }
}
