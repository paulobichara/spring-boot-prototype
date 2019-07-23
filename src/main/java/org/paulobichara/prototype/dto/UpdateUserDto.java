package org.paulobichara.prototype.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class UpdateUserDto {

    @Length(min = 8, message = "validation.password.notValid")
    private String password;

}
