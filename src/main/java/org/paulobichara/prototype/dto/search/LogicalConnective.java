package org.paulobichara.prototype.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum that represents the valid logical connectives from the API search query language
 */
@AllArgsConstructor
public enum LogicalConnective implements Token {
    AND(1),OR(2);

    @Getter
    private final int priority;

    public static LogicalConnective getLogicalConnective(final String operator) {
        if (operator.equalsIgnoreCase(AND.name())) {
            return AND;
        }
        if (operator.equalsIgnoreCase(OR.name())) {
            return OR;
        }
        return null;
    }
}
