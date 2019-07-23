package org.paulobichara.prototype.exception;

public class SearchQueryParseException extends ApiException {

    private static final long serialVersionUID = -7192219157780979329L;

    public SearchQueryParseException() {
        super("exception.parsing.searchQuery", new Object[]{});
    }

}
