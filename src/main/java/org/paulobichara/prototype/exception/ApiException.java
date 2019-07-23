package org.paulobichara.prototype.exception;

import lombok.Getter;

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1714555704191337582L;

    @Getter
    private final Object[] args;

    ApiException(String messageProperty, Object[] args) {
        super(messageProperty);
        this.args = args;
    }

}
