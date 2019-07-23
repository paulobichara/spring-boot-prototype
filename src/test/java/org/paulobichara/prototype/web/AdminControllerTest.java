package org.paulobichara.prototype.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.paulobichara.prototype.config.DefaultAdminProperties;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.repository.UserRepository;
import java.util.List;
import lombok.Getter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
class AdminControllerTest extends BaseUserRestTest {

    private static final String USER_EMAIL = "new-user@email.com";

    private static final String ADMIN_EMAIL = "new-admin@email.com";
    private static final String ADMIN2_EMAIL = "new-admin2@email.com";
    private static final String ADMIN_PASSWORD = "new-admin-password";
    private static final String ADMIN2_PASSWORD = "new-admin2-password";

    @Getter
    @LocalServerPort
    private int port;

    @Getter
    @Autowired
    private UserRepository userRepo;

    @Getter
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DefaultAdminProperties adminProperties;

    private User admin;
    private String token;

    @BeforeAll
    void setup() {
        admin = createAdmin();
        token = getAuthenticationToken(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    @BeforeEach
    void beforeEach() {
        userRepo.findByEmail(ADMIN2_EMAIL).ifPresent(userRepo::delete);
        userRepo.findByEmail(USER_EMAIL).ifPresent(userRepo::delete);
    }

    @AfterEach
    void afterEach() {
        super.tearDown();
    }

    @AfterAll
    void tearDown() {
        userRepo.findByEmail(ADMIN_EMAIL).ifPresent(userRepo::delete);
    }

    @Test
    void defaultAdminNeedsToLogin() {
        ResponseEntity<String> response = authenticate(adminProperties.getEmail(), adminProperties.getPassword());
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void defaultAdminNeedsToCreateAdmins() {
        checkCanCreateAdmins(adminProperties.getEmail(), adminProperties.getPassword(), ADMIN2_EMAIL, ADMIN2_PASSWORD);
    }

    @Test
    void adminNeedsToLogin() {
        ResponseEntity<String> response = authenticate(ADMIN_EMAIL, ADMIN_PASSWORD);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    void adminsNeedsToAccessOwnData() {
        checkCanAccessOwnData(admin.getId(), ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    @Test
    void adminNeedsToAccessOtherUsers() {
        checkCanAccessUserData(createUser(USER_EMAIL, "password").getId(), ADMIN_EMAIL, ADMIN_PASSWORD, HttpStatus.OK);
    }

    @Test
    void adminNeedsToAccessAllUsers() {
        checkCanAccessAllUsers(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    @Test
    void adminsNeedsToDeleteOtherUsers() {
        checkCanDeleteOthersData(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    @Test
    void adminsNeedsToSearchUsers() {
        assertSearch("id eq " + admin.getId() + " and email eq '" + admin.getEmail() + "'", 1);
        assertSearch("id eq " + admin.getId() + " and email ne '" + admin.getEmail() + "'", 0);
        assertSearch("id gt " + (admin.getId() - 1) + " and id lt " + (admin.getId() + 1), 1);
        assertSearch("email eq " + admin.getEmail().substring(0, admin.getEmail().indexOf('@')) + "*", 1);
    }

    private void assertSearch(String search, int totalFound) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl() + "/users")
            .queryParam("search", search);

        ResponseEntity<List<User>> response = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET,
            new HttpEntity<>(headersWithToken(token)), new ParameterizedTypeReference<List<User>>(){});

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
        assertThat(response.getBody().size(), equalTo(totalFound));
    }

    private User createAdmin() {
        String token = getAuthenticationToken(adminProperties.getEmail(), adminProperties.getPassword());
        User user = createProtectedUser(token, ADMIN_EMAIL, ADMIN_PASSWORD, getBaseUrl() + "/admins");
        assertThat(user, is(notNullValue()));
        return user;
    }

}
