package org.paulobichara.prototype.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class NewUserDto {

    @Email(message = "{validation.email.notValid}")
    @NotEmpty(message = "{validation.email.notEmpty}")
    private String email;

    @Length(min = 8, message = "{validation.password.notValid}")
    @NotEmpty(message = "{validation.password.notEmpty}")
    private String password;

}
