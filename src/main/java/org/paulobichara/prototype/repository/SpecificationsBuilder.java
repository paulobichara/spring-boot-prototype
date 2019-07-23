package org.paulobichara.prototype.repository;

import org.paulobichara.prototype.dto.search.LogicalConnective;
import org.paulobichara.prototype.dto.search.SearchCriteria;
import org.paulobichara.prototype.dto.search.Token;
import java.util.Stack;
import java.util.function.Function;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class responsible for converting a {@link Stack} containing the search expression tokens in order of higher
 * precedence into a {@link Specification}
 * @param <T>
 */
public class SpecificationsBuilder<T> {

    public Specification<T> build(Stack<Token> inOrder, Function<SearchCriteria, Specification<T>> converter) {
        Stack<Specification<T>> specStack = new Stack<>();

        while (!inOrder.isEmpty()) {
            Token token = inOrder.pop();
            if (token instanceof SearchCriteria) {
                specStack.push(converter.apply((SearchCriteria) token));
            } else {
                Specification<T> first = specStack.pop();
                Specification<T> second = specStack.pop();
                if (LogicalConnective.AND.equals(token)) {
                    specStack.push(Specification.where(first).and(second));
                } else if (LogicalConnective.OR.equals(token)) {
                    specStack.push(Specification.where(first).or(second));
                }
            }
        }

        return specStack.pop();
    }

}
