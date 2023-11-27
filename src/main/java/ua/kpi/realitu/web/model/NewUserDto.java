package ua.kpi.realitu.web.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.UserEntity;

import java.util.UUID;

@Data
public class NewUserDto {

    private UUID id;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String username;

    @NotBlank(message = "Field must be not blank")
    private String password;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String lastName;

    @NotBlank(message = "Field must be not blank")
    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String firstName;

    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String email;

    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String phone;

    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String telegram;
}
