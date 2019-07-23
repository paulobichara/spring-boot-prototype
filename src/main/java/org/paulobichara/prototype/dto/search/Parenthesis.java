package org.paulobichara.prototype.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Simple enum that represents parentheses as another token type from the API search query language
 */
@AllArgsConstructor
public enum Parenthesis implements Token {
    LEFT('('), RIGHT(')');

    @Getter
    private final char token;

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return String.valueOf(token);
    }
}
