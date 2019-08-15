package org.paulobichara.prototype.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import org.paulobichara.prototype.dto.search.SearchCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class representing a {@link Specification} that can be created from a {@link SearchCriteria}
 */
@NoArgsConstructor
@AllArgsConstructor
public class EntitySpecification<T> implements Specification<T> {

    @Getter
    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                if (criteria.getValue() instanceof LocalDate) {
                    return builder.greaterThan(root.get(criteria.getKey()), (LocalDate) criteria.getValue());
                } else if (criteria.getValue() instanceof LocalTime) {
                    return builder.greaterThan(root.get(criteria.getKey()), (LocalTime) criteria.getValue());
                } else {
                    return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
                }
            case LESS_THAN:
                if (criteria.getValue() instanceof LocalDate) {
                    return builder.lessThan(root.get(criteria.getKey()), (LocalDate) criteria.getValue());
                } else if (criteria.getValue() instanceof LocalTime) {
                    return builder.lessThan(root.get(criteria.getKey()), (LocalTime) criteria.getValue());
                } else {
                    return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
                }
            case LIKE:
            case STARTS_WITH:
            case ENDS_WITH:
            case CONTAINS:
                return builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
            default:
                return null;
        }
    }

}