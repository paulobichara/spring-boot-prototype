package org.paulobichara.prototype.dto.search;

/**
 * Interface indicating a generic token from the search query language
 */
public interface Token {

    /**
     * Method that returns the precedence priority value (lower priority -> higher precedence)
     * @return priority value
     */
    int getPriority();

}
