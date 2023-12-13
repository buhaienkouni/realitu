package ua.kpi.realitu.web.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.realitu.domain.enums.Category;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentDto {

    private String amount;

    private String token;

    private String cardholder;

    private String storyId;

}
