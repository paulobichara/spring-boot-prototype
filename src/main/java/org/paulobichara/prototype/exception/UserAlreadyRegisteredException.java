package org.paulobichara.prototype.exception;

public class UserAlreadyRegisteredException extends ApiException {

    private static final long serialVersionUID = -1681171216111738198L;

    private static final String MESSAGE_PROPERTY = "exception.alreadyRegistered.user";

    public UserAlreadyRegisteredException(String email) {
        super(MESSAGE_PROPERTY, new Object[] { email });
    }
}
