package ru.bvn13.voidforum.forms;

import lombok.Data;


import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * bvn13 <mail4bvn@gmail.com>.
 */
@Data
public class RegistrationForm {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Specify your email")
    private String username;

    @NotBlank(message = "Specify your nickname")
    private String nickname;

    @NotBlank(message = "Specify your password")
    private String password;

    @NotBlank(message = "Verify your password")
    private String passwordCheck;

}
