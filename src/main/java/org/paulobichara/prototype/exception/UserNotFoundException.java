package org.paulobichara.prototype.exception;

public class UserNotFoundException extends ApiException {

    private static final long serialVersionUID = -4138427568025207335L;

    private static final String MESSAGE_PROPERTY_ID = "exception.notFound.user.id";
    private static final String MESSAGE_PROPERTY_EMAIL = "exception.notFound.user.email";

    public UserNotFoundException(String email) {
        super(MESSAGE_PROPERTY_EMAIL, new Object[] { email });
    }

    public UserNotFoundException(Long userId) {
        super(MESSAGE_PROPERTY_ID, new Object[] { userId });
    }

}
