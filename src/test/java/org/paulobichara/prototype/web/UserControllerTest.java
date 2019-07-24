package org.paulobichara.prototype.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.paulobichara.prototype.dto.UpdateUserDto;
import org.paulobichara.prototype.model.Role;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest extends BaseUserRestTest {

    private static final String USER_EMAIL = "new-user@email.com";
    private static final String USER_PASSWORD = "new-user-password";
    private static final String USER2_EMAIL = "new-user2@email.com";

    private static final String CHANGED_PASSWORD = "changed-password";
    private static final String INVALID_PASSWORD = "invalid-password";

    @Getter
    @LocalServerPort
    private int port;

    @Getter
    @Autowired
    private UserRepository userRepo;

    @Getter
    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    void tearDown() {
        userRepo.findByEmail(USER_EMAIL).ifPresent(userRepo::delete);
        userRepo.findByEmail(USER2_EMAIL).ifPresent(userRepo::delete);
    }

    @Test
    @SneakyThrows(URISyntaxException.class)
    void anonymousUsersShouldNotAccessProtectedResource() {
        User user = createUser(USER_EMAIL, USER_PASSWORD);

        ResponseEntity<String> response =
                restTemplate.getForEntity(new URI(getBaseUrl() + "/users/" + user.getId()), String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));

        response = restTemplate.getForEntity(new URI(getBaseUrl() + "/users"), String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void usersNeedsToCreateAccount() {
        User user = createUser("new-user@email.com", "new-user-password");
        assertThat(user.getPassword(), is(nullValue()));
        assertThat(user.getId(), is(notNullValue()));
        assertThat(user.getEmail(), equalTo(USER_EMAIL));

        User found = userRepo.findById(user.getId()).orElse(null);
        assertThat(found, is(notNullValue()));
        assertThat(found.getRoles().size(), equalTo(1));
        assertThat(found.getRoles(), CoreMatchers.hasItem(Role.ROLE_USER));
    }

    @Test
    void usersNeedsToLogin() {
        createUser(USER_EMAIL, USER_PASSWORD);
        ResponseEntity<String> response = authenticate(USER_EMAIL, USER_PASSWORD);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void loggedUsersNeedsToAccessOwnData() {
        User user = createUser(USER_EMAIL, USER_PASSWORD);
        checkCanAccessOwnData(user.getId(), USER_EMAIL, USER_PASSWORD);
    }

    @Test
    void loggedUsersNeedsToUpdateOwnData() {
        User user = createUser(USER_EMAIL, USER_PASSWORD);
        String token = getAuthenticationToken(USER_EMAIL, USER_PASSWORD);

        HttpHeaders headers = headersWithToken(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setPassword(CHANGED_PASSWORD);

        restTemplate.put(getBaseUrl() + "/users/" + user.getId(), new HttpEntity<>(dto, headers));

        getAuthenticationToken(USER_EMAIL, CHANGED_PASSWORD);
    }

    @Test
    void loggedUsersNeedsToDeleteOwnData() {
        User user = createUser(USER_EMAIL, USER_PASSWORD);
        String token = getAuthenticationToken(USER_EMAIL, USER_PASSWORD);

        ResponseEntity<User> response = restTemplate.exchange(getBaseUrl() + "/users/" + user.getId(),
                HttpMethod.DELETE, new HttpEntity<>(headersWithToken(token)), User.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().getId(), equalTo(user.getId()));
    }

    @Test
    void loggedUsersCantAccessOthersData() {
        createUser(USER_EMAIL, USER_PASSWORD);
        checkCanAccessUserData(createUser(USER2_EMAIL, USER_PASSWORD).getId(), USER_EMAIL, USER_PASSWORD,
                HttpStatus.FORBIDDEN);
    }

    @Test
    void usersShouldNotLoginWithInvalidCredentials() {
        ResponseEntity<String> response = authenticate(USER_EMAIL, INVALID_PASSWORD);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
    }

}
