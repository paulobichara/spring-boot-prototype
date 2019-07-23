package org.paulobichara.prototype.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonInclude(Include.NON_NULL)
public class ApiValidationError {

    private String field;
    private Object rejectedValue;
    private String message;

}
