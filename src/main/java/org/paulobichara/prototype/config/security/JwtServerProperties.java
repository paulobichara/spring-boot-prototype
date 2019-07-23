package org.paulobichara.prototype.config.security;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "api.jwt")
@Getter
@Setter
@NoArgsConstructor
@Validated
public class JwtServerProperties {

    @NotEmpty(message = "You must provide a Base64 encoded secret for JWT signing")
    private String secret;

}
