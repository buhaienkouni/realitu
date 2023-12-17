package ua.kpi.realitu.service.converter;

import org.springframework.stereotype.Component;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.web.model.ArticleDto;

import java.time.LocalDateTime;

@Component
public class ArticleDtoToEntityConverter {

    public Article create(ArticleDto articleDto, UserEntity principalUser) {
        Article articleEntity = new Article();

        articleEntity.setTitle(articleDto.getTitle().trim());
        articleEntity.setKeywords(articleDto.getKeywords().trim());
        articleEntity.setContent(articleDto.getContent());
        articleEntity.setCreationDate(LocalDateTime.now());
        articleEntity.setCategory(articleDto.getCategory());
        articleEntity.setImageTitle(articleDto.getImageTitle().trim());
        articleEntity.setDonations(articleDto.getDonations());
        articleEntity.setMonoLink(articleDto.getMonoLink().trim());
        articleEntity.setAuthor(principalUser);

        return articleEntity;
    }

    public Article update(ArticleDto articleDto, Article articleEntity) {

        articleEntity.setTitle(articleDto.getTitle().trim());
        articleEntity.setKeywords(articleDto.getKeywords().trim());
        articleEntity.setContent(articleDto.getContent());
        articleEntity.setCategory(articleDto.getCategory());
        articleEntity.setImageTitle(articleDto.getImageTitle().trim());
        articleEntity.setDonations(articleDto.getDonations());
        articleEntity.setMonoLink(articleDto.getMonoLink().trim());

        return articleEntity;
    }
}
