package org.paulobichara.prototype.web;

import org.paulobichara.prototype.dto.error.ApiError;
import org.paulobichara.prototype.dto.error.ApiValidationError;
import org.paulobichara.prototype.exception.ApiException;
import org.paulobichara.prototype.exception.SearchQueryParseException;
import org.paulobichara.prototype.exception.UserAlreadyRegisteredException;
import org.paulobichara.prototype.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String VALIDATION_FAILED = "api.errors.validation";
    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(UserNotFoundException.class)
    protected ResponseEntity handleNotFoundApiExceptions(ApiException exception, Locale locale) {
        return handleApiException(HttpStatus.NOT_FOUND, exception, locale);
    }

    @ExceptionHandler({SearchQueryParseException.class, UserAlreadyRegisteredException.class})
    protected ResponseEntity handleBadRequestApiExceptions(ApiException exception, Locale locale) {
        return handleApiException(HttpStatus.BAD_REQUEST, exception, locale);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity handleConstraintViolation(ConstraintViolationException exception) {
        log.error("Unexpected constraint violation occurred", exception);
        return ResponseEntity.badRequest().build();
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, messageSource.getMessage(VALIDATION_FAILED,
                new Object[]{}, request.getLocale()));

        List<ApiValidationError> subErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                subErrors.add(new ApiValidationError(fieldError.getField(), fieldError.getRejectedValue(),
                    fieldError.getDefaultMessage()));
            } else {
                subErrors.add(new ApiValidationError(null, null, error.getDefaultMessage()));
            }
        });

        apiError.setValidationErrors(subErrors);

        return new ResponseEntity<>(apiError, headers, status);
    }

    private ResponseEntity<ApiError> handleApiException(HttpStatus status, ApiException exception, Locale locale) {
        ApiError error =
                new ApiError(status, messageSource.getMessage(exception.getMessage(), exception.getArgs(), locale));

        return new ResponseEntity<>(error, error.getStatus());
    }

}
