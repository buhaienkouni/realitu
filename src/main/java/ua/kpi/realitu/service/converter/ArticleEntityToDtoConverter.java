package ua.kpi.realitu.service.converter;

import org.springframework.stereotype.Component;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.enums.Category;
import ua.kpi.realitu.web.model.ArticleDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class ArticleEntityToDtoConverter {

    public ArticleDto convert(Article articleEntity) {
        ArticleDto articleDto = new ArticleDto();

        articleDto.setId(articleEntity.getId());
        articleDto.setTitle(articleEntity.getTitle());
        articleDto.setTitleForLink(convertTitleForLink(articleEntity.getTitle()));
        articleDto.setContent(articleEntity.getContent());
        articleDto.setCreationDateString(convertDataToString(articleEntity.getCreationDate()));
        articleDto.setCategory(articleEntity.getCategory());

        if (articleEntity.getCategory() == Category.HISTORY &&
                articleEntity.getCardNumber() != null &&
                !articleEntity.getCardNumber().isEmpty()) {
            articleDto.setCardNumber(convertCardNumber(articleEntity.getCardNumber()));
            articleDto.setCardOwner(articleEntity.getCardOwner().trim());
        }
        if (articleEntity.getCategory() == Category.HISTORY &&
                articleEntity.getMonoLink() != null &&
                !articleEntity.getMonoLink().isEmpty()) {
            articleDto.setMonoLink(articleEntity.getMonoLink());
        }

        articleDto.setImageTitle(articleEntity.getImageTitle());
        articleDto.setAuthorName(articleEntity.getAuthor().getUsername());

        return articleDto;
    }

    public ArticleDto convertForEditing(Article articleEntity) {
        ArticleDto articleDto = new ArticleDto();

        articleDto.setId(articleEntity.getId());
        articleDto.setTitle(articleEntity.getTitle());
        articleDto.setContent(articleEntity.getContent());
        articleDto.setCreationDateString(convertDataToString(articleEntity.getCreationDate()));
        articleDto.setCategory(articleEntity.getCategory());
        articleDto.setCardNumber(articleEntity.getCardNumber());
        articleDto.setCardOwner(articleEntity.getCardOwner());
        articleDto.setMonoLink(articleEntity.getMonoLink());
        articleDto.setImageTitle(articleEntity.getImageTitle());

        Optional.ofNullable(articleEntity.getImage())
                .ifPresent(image -> articleDto.setImageId(image.getId()));

        articleDto.setAuthorName(articleEntity.getAuthor().getUsername());

        return articleDto;
    }

    private String convertDataToString(LocalDateTime creationDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return creationDate.format(formatter);
    }

    public String convertTitleForLink(String title) {
        title = title.trim().toLowerCase();
        title = title.replaceAll("\\s+", "-");

        return title;
    }

    public String convertCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return "Invalid card number format";
        }
        return cardNumber.replaceAll("(.{4})", "$1 ").trim();
    }
}
