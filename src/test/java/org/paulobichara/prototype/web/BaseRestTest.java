package org.paulobichara.prototype.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.model.User;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

abstract class BaseRestTest {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    abstract TestRestTemplate getRestTemplate();

    abstract int getPort();

    ResponseEntity<String> authenticate(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(KEY_USERNAME, username);
        map.add(KEY_PASSWORD, password);

        return getRestTemplate().postForEntity(getBaseUrl() + "/login", new HttpEntity<>(map, headers), String.class);
    }

    @SneakyThrows(URISyntaxException.class)
    User createUser(String email, String password) {
        NewUserDto dto = new NewUserDto();
        dto.setPassword(password);
        dto.setEmail(email);

        ResponseEntity<User> response =
            getRestTemplate().postForEntity(new URI(getBaseUrl() + "/users"), dto, User.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        return response.getBody();
    }

    String getBaseUrl() {
        return "http://localhost:" + getPort() + "/api";
    }

    HttpHeaders headersWithToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, token);
        return headers;
    }

    String getAuthenticationToken(String email, String password) {
        ResponseEntity<String> response = authenticate( email, password);
        List<String> tokenValues = response.getHeaders().get(HttpHeaders.AUTHORIZATION);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(tokenValues, is(notNullValue()));
        assertThat(tokenValues.size(), equalTo(1));
        assertThat(tokenValues.get(0), is(notNullValue()));
        return tokenValues.get(0);
    }

}
