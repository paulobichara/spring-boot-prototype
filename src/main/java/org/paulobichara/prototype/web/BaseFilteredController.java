package org.paulobichara.prototype.web;

import org.paulobichara.prototype.repository.EntitySpecification;
import org.paulobichara.prototype.repository.SpecificationsBuilder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

abstract class BaseFilteredController<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    static final String SYNTAX = "Search query to be executed. Valid operations are: eq (equals), "
            + "ne (not equals), gt (greater than), lt (less than). Logical operator are: and, or. Parentheses are "
            + "supported. Finally, query syntax also supports the use of * as wildcard.";

    Specification<T> parseSearchQuery(String searchQuery) {
        if (searchQuery == null) {
            return null;
        }
        SearchQueryParser parser = new SearchQueryParser();
        SpecificationsBuilder<T> specBuilder = new SpecificationsBuilder<>();
        return specBuilder.build(parser.parse(searchQuery), EntitySpecification::new);
    }

    String decodeSearchParameter(String search) throws UnsupportedEncodingException {
        if (search == null) {
            return null;
        }

        try {
            return URLDecoder.decode(search, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException exception) {
            LOGGER.error(String.format("Unpredicted error when trying to decode search URL parameter [%s]", search),
                exception);
            throw exception;
        }
    }

}
