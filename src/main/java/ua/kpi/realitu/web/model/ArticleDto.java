package ua.kpi.realitu.web.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import ua.kpi.realitu.domain.enums.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ArticleDto {

    private UUID id;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 200, message = "Maximum size of field is 200 characters")
    private String title;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 60000, message = "Maximum size of field is 60.000 characters")
    private String content;

    private LocalDateTime creationDate;

    @NotNull
    private Category category;

//TODO: Finnish DTO

}
