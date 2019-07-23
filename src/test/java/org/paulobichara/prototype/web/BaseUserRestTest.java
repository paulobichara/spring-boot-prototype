package org.paulobichara.prototype.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.model.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

abstract class BaseUserRestTest extends BaseRestTest {

    User createProtectedUser(String token, String email, String password, String url) {
        NewUserDto dto = new NewUserDto();
        dto.setPassword(password);
        dto.setEmail(email);

        ResponseEntity<User> response =
                getRestTemplate().postForEntity(url, new HttpEntity<>(dto, headersWithToken(token)), User.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        return response.getBody();
    }

    void checkCanAccessOwnData(long userId, String email, String password) {
        String token = getAuthenticationToken(email, password);

        ResponseEntity<User> userResponse = getRestTemplate().exchange(getBaseUrl() + "/users/" + userId,
                HttpMethod.GET, new HttpEntity<>(headersWithToken(token)), User.class);

        assertThat(userResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(userResponse.getBody(), is(notNullValue()));
        assertThat(userResponse.getBody().getId(), equalTo(userId));
    }

    void checkCanAccessUserData(long userId, String email, String password, HttpStatus status) {
        String token = getAuthenticationToken(email, password);
        assertThat(token, is(notNullValue()));

        ResponseEntity<User> response = getRestTemplate().exchange(getBaseUrl() + "/users/" + userId,
                HttpMethod.GET, new HttpEntity<>(headersWithToken(token)), User.class);

        assertThat(response.getStatusCode(), equalTo(status));
        if (HttpStatus.OK.equals(status)) {
            assertThat(response.getBody(), is(notNullValue()));
            assertThat(response.getBody().getId(), equalTo(userId));
        }
    }
}
