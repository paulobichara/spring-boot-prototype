package org.paulobichara.prototype.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.repository.UserRepository;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

abstract class BaseUserRestTest extends BaseRestTest {

    private static final String USER_EMAIL = "user@email.com";
    private static final String USER_PASSWORD = "user-password";

    abstract UserRepository getUserRepo();

    void tearDown() {
        getUserRepo().findByEmail(USER_EMAIL).ifPresent(getUserRepo()::delete);
    }

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

    void checkCanDeleteOthersData(String email, String password) {
        User user = createUser(USER_EMAIL, USER_PASSWORD);
        assertThat(user, is(notNullValue()));

        String token = getAuthenticationToken(email, password);
        assertThat(token, is(notNullValue()));

        ResponseEntity<User> response = getRestTemplate().exchange(getBaseUrl() + "/users/" + user.getId(),
                HttpMethod.DELETE, new HttpEntity<>(headersWithToken(token)), User.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getId(), equalTo(user.getId()));
    }

    void checkCanAccessOwnData(long userId, String email, String password) {
        String token = getAuthenticationToken(email, password);

        ResponseEntity<User> userResponse = getRestTemplate().exchange(getBaseUrl() + "/users/" + userId,
                HttpMethod.GET, new HttpEntity<>(headersWithToken(token)), User.class);

        assertThat(userResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(userResponse.getBody(), is(notNullValue()));
        assertThat(userResponse.getBody().getId(), equalTo(userId));
    }

    void checkCanAccessAllUsers(String email, String password) {
        String token = getAuthenticationToken(email, password);
        assertThat(token, is(notNullValue()));

        createUser(USER_EMAIL, USER_PASSWORD);
        ResponseEntity<List<User>> listResponse = getRestTemplate().exchange(getBaseUrl() + "/users",
                HttpMethod.GET, new HttpEntity<>(headersWithToken(token)),
                new ParameterizedTypeReference<List<User>>(){});

        assertThat(listResponse.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(listResponse.getBody(), is(notNullValue()));
        assertThat(listResponse.getBody().size(), equalTo(3));
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

    void checkCanCreateAdmins(String email, String password, String adminEmail, String adminPassword) {
        String token = getAuthenticationToken(email, password);
        User user = createProtectedUser(token, adminEmail, adminPassword, getBaseUrl() + "/admins");
        assertThat(user, is(notNullValue()));
        assertThat(user.getPassword(), is(nullValue()));
        assertThat(user.getId(), is(notNullValue()));
        assertThat(user.getEmail(), equalTo(adminEmail));
    }
}
