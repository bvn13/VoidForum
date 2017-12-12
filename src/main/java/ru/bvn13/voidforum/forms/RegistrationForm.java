package ru.bvn13.voidforum.forms;

import lombok.Data;


import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * bvn13 <mail4bvn@gmail.com>.
 */
@Data
public class RegistrationForm implements Serializable {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Specify your email")
    private String username = "";

    @Min(value = 4, message = "Nickname should be longer than 4 characters")
    @Pattern(regexp = "\\w", message = "Nickname must not contain spaces")
    @NotBlank(message = "Specify your nickname")
    private String nickname = "";

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$", message = "Password should be longer than 6 characters, at least one latin letter and one number")
    @NotBlank(message = "Specify your password")
    private String password;

    @NotBlank(message = "Verify your password")
    private String passwordCheck;

}
