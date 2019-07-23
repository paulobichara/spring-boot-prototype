package org.paulobichara.prototype.web;

import org.paulobichara.prototype.dto.search.FieldOperation;
import org.paulobichara.prototype.dto.search.LogicalConnective;
import org.paulobichara.prototype.dto.search.Parenthesis;
import org.paulobichara.prototype.dto.search.SearchCriteria;
import org.paulobichara.prototype.dto.search.Token;
import org.paulobichara.prototype.exception.SearchQueryParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchQueryParser {

    public Stack<Token> parse(String searchQuery) {
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = regex.matcher(searchQuery);

        Stack<Token> inOrder = new Stack<>();

        Stack<Token> tokens = new Stack<>();

        while (matcher.find()) {
            String token = matcher.group();
            LogicalConnective connective = LogicalConnective.getLogicalConnective(token);
            if (connective != null) {
                handleLogicalConnective(connective, tokens, inOrder);
            } else if (token.startsWith(Parenthesis.LEFT.toString())) {
                handleLeftParenthesis(token, matcher, tokens, inOrder);
            } else if (token.startsWith(Parenthesis.RIGHT.toString())) {
                handleRightParenthesis(token, tokens, inOrder);
            } else {
                handleNewSearchCriteria(token, matcher, tokens, inOrder);
            }
        }

        while (!tokens.isEmpty()) {
            if (!(tokens.peek() instanceof LogicalConnective)) {
                throw new SearchQueryParseException();
            }
            inOrder.push(tokens.pop());
        }

        Collections.reverse(inOrder);

        return inOrder;
    }

    private void handleLogicalConnective(LogicalConnective connective, Stack<Token> tokens, Stack<Token> inOrder) {
        while (!tokens.isEmpty() && tokens.peek().getPriority() < connective.getPriority()) {
            inOrder.push(tokens.pop());
        }
        tokens.push(connective);
    }

    private void handleLeftParenthesis(String token, Matcher matcher, Stack<Token> tokens, Stack<Token> inOrder) {
        int tokenIndex = 0;
        while (tokenIndex < token.length() && token.charAt(tokenIndex) == Parenthesis.LEFT.getToken()) {
            tokens.push(Parenthesis.LEFT);
            tokenIndex++;
        }
        if (tokenIndex < token.length()) {
            handleNewSearchCriteria(token.substring(tokenIndex), matcher, tokens, inOrder);
        }
    }

    private void handleRightParenthesis(String token, Stack<Token> tokens, Stack<Token> inOrder) {
        int tokenIndex = 0;
        while (tokenIndex < token.length() && token.charAt(tokenIndex) == Parenthesis.RIGHT.getToken()) {
            while (!tokens.isEmpty() && !Parenthesis.LEFT.equals(tokens.peek())) {
                if (!(tokens.peek() instanceof LogicalConnective)) {
                    throw new SearchQueryParseException();
                }
                inOrder.push(tokens.pop());
            }
            if (tokens.isEmpty()) {
                throw new SearchQueryParseException();
            }
            tokens.pop();
            tokenIndex++;
        }

        if (tokenIndex < token.length()) {
            throw new SearchQueryParseException();
        }
    }

    private void handleNewSearchCriteria(String key, Matcher matcher, Stack<Token> tokens, Stack<Token> inOrder) {
        FieldOperation operation;
        if (matcher.find()
                && (operation = FieldOperation.getOperation(matcher.group())) != null
                && matcher.find()) {
            String value;
            if (((value = matcher.group(1)) != null) || ((value = matcher.group(2)) != null)) {
                inOrder.push(new SearchCriteria(key, operation, getRealValue(value)));
                return;
            } else if ((value = matcher.group()) != null) {
                int endIndex = value.indexOf(Parenthesis.RIGHT.toString());
                if (endIndex >= 1) {
                    inOrder.push(new SearchCriteria(key, operation, getRealValue(value.substring(0, endIndex))));
                    handleRightParenthesis(value.substring(endIndex), tokens, inOrder);
                } else {
                    inOrder.push(new SearchCriteria(key, operation, getRealValue(value)));
                }
                return;
            }
        }
        throw new SearchQueryParseException();
    }

    private Object getRealValue(String value) {
        try {
            if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                return LocalDate.parse(value);
            } else if (value.matches("^\\d{2}:\\d{2}:\\d{2}$")) {
                return LocalTime.parse(value);
            }
        } catch (DateTimeParseException exception) {
            return value;
        }
        return value;
    }

}
