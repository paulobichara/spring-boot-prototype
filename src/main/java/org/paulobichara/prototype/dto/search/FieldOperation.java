package org.paulobichara.prototype.dto.search;

/**
 * Enum that represents a field operation from the API search query language
 */
public enum FieldOperation {

    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS;

    public static FieldOperation getOperation(final String operation) {
        switch (operation) {
            case "eq":
                return EQUALITY;
            case "ne":
                return NEGATION;
            case "gt":
                return GREATER_THAN;
            case "lt":
                return LESS_THAN;
            default:
                return null;
        }
    }
}
