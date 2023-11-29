package ua.kpi.realitu.service.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.UserEntity;
import ua.kpi.realitu.repository.UserRepository;
import ua.kpi.realitu.web.model.ArticleDto;
import ua.kpi.realitu.web.model.NewUserDto;
import ua.kpi.realitu.web.model.UserDto;

import java.time.LocalDateTime;

@Component
public class ArticleDtoToEntityConverter {

    @Autowired
    private UserRepository userRepository;

    public Article create(ArticleDto articleDto, UserEntity principalUser) {
        Article articleEntity = new Article();

        articleEntity.setTitle(articleDto.getTitle().strip());
        articleEntity.setContent(articleDto.getContent().strip());
        articleEntity.setCreationDate(LocalDateTime.now());
        articleEntity.setCategory(articleDto.getCategory());
        articleEntity.setImageTitle(articleDto.getImageTitle());
        articleEntity.setAuthor(principalUser);

        return articleEntity;
    }

    public Article update(ArticleDto articleDto, Article articleEntity) {

        articleEntity.setTitle(articleDto.getTitle().strip());
        articleEntity.setContent(articleDto.getContent().strip());
        articleEntity.setCategory(articleDto.getCategory());
        articleEntity.setImageTitle(articleDto.getImageTitle());

        return articleEntity;
    }
}
