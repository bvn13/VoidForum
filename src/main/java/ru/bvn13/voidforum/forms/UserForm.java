package ru.bvn13.voidforum.forms;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Email;

/**
 * bvn13 <mail4bvn@gmail.com>.
 */
@Data
public class UserForm {

    @NotBlank(message = "Enter your password")
    private String password;

    @NotBlank(message = "Specify your new password")
    private String newPassword;

}
