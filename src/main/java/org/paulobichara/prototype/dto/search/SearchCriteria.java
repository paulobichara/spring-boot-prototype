package org.paulobichara.prototype.dto.search;

import lombok.Getter;
import lombok.Setter;

/**
 * Parsed search criteria from a search query
 */
@Getter
@Setter
public class SearchCriteria implements Token {

    private static final char WILDCARD = '*';
    private static final char PERCENT = '%';

    private String key;
    private FieldOperation operation;
    private Object value;

    private boolean orPredicate;

    @Override
    public int getPriority() {
        return 0;
    }

    public SearchCriteria(String key, FieldOperation operation, Object value) {
        if (FieldOperation.EQUALITY.equals(operation) && value instanceof String) {
            final boolean startWithAsterisk = String.valueOf(value).startsWith(String.valueOf(WILDCARD));
            final boolean endWithAsterisk = String.valueOf(value).endsWith(String.valueOf(WILDCARD));

            if (startWithAsterisk && endWithAsterisk) {
                this.operation = FieldOperation.CONTAINS;
            } else if (startWithAsterisk) {
                this.operation = FieldOperation.ENDS_WITH;
            } else if (endWithAsterisk) {
                this.operation = FieldOperation.STARTS_WITH;
            } else {
                this.operation = operation;
            }
            value = prepareStringValue(value);
        } else {
            this.operation = operation;
        }
        this.key = key;
        this.value = value;
    }

    private String prepareStringValue(Object value) {
        char[] valueCharArray = String.valueOf(value).toCharArray();
        if (valueCharArray[0] == WILDCARD) {
            valueCharArray[0] = PERCENT;
        }
        if (valueCharArray[valueCharArray.length - 1] == WILDCARD) {
            valueCharArray[valueCharArray.length - 1] = PERCENT;
        }
        return new String(valueCharArray);
    }
}
