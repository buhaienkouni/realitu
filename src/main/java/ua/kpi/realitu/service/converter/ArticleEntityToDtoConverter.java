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
        articleDto.setKeywords(articleEntity.getKeywords());
        articleDto.setTitleForLink(convertTitleForLink(articleEntity.getTitle()));
        articleDto.setContent(formatContent(articleEntity.getContent()));
        articleDto.setPreviewContent(createPreview(articleEntity.getContent(), articleEntity.getTitle()));
        articleDto.setCreationDate(articleEntity.getCreationDate());
        articleDto.setCreationDateString(convertDataToString(articleEntity.getCreationDate()));
        articleDto.setCategory(articleEntity.getCategory());

        if (articleEntity.getCategory() == Category.HISTORY) {
            articleDto.setDonations(articleEntity.getDonations());
            if (articleEntity.getMonoLink() != null && !articleEntity.getMonoLink().isEmpty()) {
                articleDto.setMonoLink(articleEntity.getMonoLink());
            }
        }

        articleDto.setImageTitle(articleEntity.getImageTitle());
        articleDto.setAuthorName(articleEntity.getAuthor().getUsername());

        return articleDto;
    }

    public ArticleDto convertForEditing(Article articleEntity) {
        ArticleDto articleDto = new ArticleDto();

        articleDto.setId(articleEntity.getId());
        articleDto.setTitle(articleEntity.getTitle());
        articleDto.setKeywords(articleEntity.getKeywords());
        articleDto.setContent(articleEntity.getContent());
        articleDto.setCreationDateString(convertDataToString(articleEntity.getCreationDate()));
        articleDto.setCategory(articleEntity.getCategory());
        articleDto.setDonations(articleEntity.getDonations());
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

    public String createPreview(String content, String title) {
        int titleLength = title.length();

        int endIndex;
        if (titleLength <= 65) {
            endIndex = Math.min(content.length(), 340);
            if (endIndex == 340) {
                while (endIndex > 330 && isSpaceOrPunctuation(content.charAt(endIndex - 1))) {
                    endIndex--;
                }
            }
        } else {
            endIndex = Math.min(content.length(), 230);
            if (endIndex == 230) {
                while (endIndex > 220 && isSpaceOrPunctuation(content.charAt(endIndex - 1))) {
                    endIndex--;
                }
            }
        }

        if (endIndex > 0 && isSpaceOrPunctuation(content.charAt(endIndex - 1))) {
            endIndex--;
        }

        return content.substring(0, endIndex) + "...";
    }

    private boolean isSpaceOrPunctuation(char c) {
        return c == ' ' || c == '.' || c == ',';
    }

    public String formatContent(String content) {
        content = content.replaceAll("\n", "<br>");

        return content;
    }
}
