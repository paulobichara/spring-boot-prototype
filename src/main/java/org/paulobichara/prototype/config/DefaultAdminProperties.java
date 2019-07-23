package org.paulobichara.prototype.config;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "api.admin")
@Getter
@Setter
@NoArgsConstructor
@Validated
public class DefaultAdminProperties {

    @Email(message = "Default admin email must be valid")
    @NotEmpty(message = "Default admin email must be provided")
    private String email;

    @Length(min = 8, message = "Default admin password must be valid")
    @NotEmpty(message = "Default admin password must be provided")
    private String password;

}
