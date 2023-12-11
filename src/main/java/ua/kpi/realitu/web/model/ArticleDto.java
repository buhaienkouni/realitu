package ua.kpi.realitu.web.model;


import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.realitu.domain.enums.Category;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ArticleDto {

    private UUID id;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 300, message = "Maximum size of field is 300 characters")
    private String title;

    private String titleForLink;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 100000, message = "Maximum size of field is 100.000 characters")
    private String content;

    private String previewContent;

    private LocalDateTime creationDate;

    private String creationDateString;

    @NotNull
    private Category category;

    @Size(max = 300, message = "Maximum size of field is 300 characters")
    private String imageTitle;

    // Donations
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Integer donations;

    private String monoLink;

    // Author
    private String authorName;

    private UUID authorId;

    // Image
    private UUID imageId;

    private MultipartFile image;

}
