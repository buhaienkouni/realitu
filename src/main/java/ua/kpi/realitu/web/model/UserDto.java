package ua.kpi.realitu.web.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {

    private UUID id;

    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String username;

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

    @NotBlank(message = "Field must be not blank")
    @Size(max = 50, message = "Maximum size of field is 50 characters")
    private String telegram;

}
