package org.paulobichara.prototype.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.paulobichara.prototype.dto.search.FieldOperation;
import org.paulobichara.prototype.dto.search.LogicalConnective;
import org.paulobichara.prototype.dto.search.SearchCriteria;
import org.paulobichara.prototype.dto.search.Token;
import org.paulobichara.prototype.exception.SearchQueryParseException;
import org.paulobichara.prototype.web.SearchQueryParser;
import java.time.LocalDate;
import java.util.Stack;
import org.junit.jupiter.api.Test;

class CriteriaParserTest {

    private final SearchQueryParser parser = new SearchQueryParser();

    @Test
    void testValidQueries() {
        Stack<Token> inOrder =
                parser.parse("(date eq '2016-05-01') AND ((number_field_1 gt 20) OR (number_field_2 lt 10))");
        assertInOrderStack(inOrder);
        inOrder = parser.parse("( date eq 2016-05-01 ) AND ( ( number_field_1 gt 20 ) OR ( number_field_2 lt 10 ) ) ");
        assertInOrderStack(inOrder);
        inOrder = parser.parse("date eq \"2016-05-01\" AND ( ( number_field_1 gt 20 ) OR ( number_field_2 lt 10 ) ) ");
        assertInOrderStack(inOrder);
    }

    @Test
    void testInvalidQueries() {
        assertThrows(SearchQueryParseException.class, () -> parser.parse("((date eq '2016-05-01')"));
        assertThrows(SearchQueryParseException.class, () -> parser.parse("(date eq '2016-05-01'))"));
        assertThrows(SearchQueryParseException.class, () -> parser.parse("date date '2016-05-01'))"));
        assertThrows(SearchQueryParseException.class, () -> parser.parse("'2016-05-01'"));
        assertThrows(SearchQueryParseException.class, () -> parser.parse("eq"));
    }

    private void assertInOrderStack(Stack<Token> inOrder) {
        assertThat(inOrder.size(), equalTo(5));
        assertSearchCriteria(inOrder.pop(), "date", FieldOperation.EQUALITY, LocalDate.parse("2016-05-01"));
        assertSearchCriteria(inOrder.pop(), "number_field_1", FieldOperation.GREATER_THAN, "20");
        assertSearchCriteria(inOrder.pop(), "number_field_2", FieldOperation.LESS_THAN, "10");
        assertThat(inOrder.pop(), equalTo(LogicalConnective.OR));
        assertThat(inOrder.pop(), equalTo(LogicalConnective.AND));
    }

    private void assertSearchCriteria(Token token, String key, FieldOperation operation, Object value) {
        assertThat(token instanceof SearchCriteria, equalTo(true));
        if (token instanceof SearchCriteria) {
            assertThat(((SearchCriteria) token).getKey(), equalTo(key));
            assertThat(((SearchCriteria) token).getOperation(), equalTo(operation));
            assertThat(((SearchCriteria) token).getValue(), equalTo(value));
        }
    }

}
