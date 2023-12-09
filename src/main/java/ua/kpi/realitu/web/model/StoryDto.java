package ua.kpi.realitu.web.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoryDto {

    @NotBlank(message = "Field must be not blank")
    @Size(max = 300, message = "Maximum size of field is 300 characters")
    private String personalData;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 100000, message = "Maximum size of field is 100.000 characters")
    private String story;

}
