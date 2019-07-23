package org.paulobichara.prototype.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.paulobichara.prototype.model.Role;
import org.paulobichara.prototype.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

    private static final String EMAIL = "fake@email.com";
    private static final String PASSWORD = "password";

    @Autowired
    private UserRepository userRepo;

    @Test
    void registeredUserShouldBeFoundByEmail() {
        userRepo.save(createUser());
        Optional<User> optional = userRepo.findByEmail(EMAIL);
        User found = optional.orElse(null);
        assertThat(found, is(notNullValue()));
        assertThat(found.getEmail(), equalTo(EMAIL));
        assertThat(found.getPassword(), equalTo(PASSWORD));
        assertThat(found.getRoles().size(), equalTo(3));
        assertThat(found.getRoles(), CoreMatchers.hasItems(Role.ROLE_USER_MANAGER, Role.ROLE_ADMIN, Role.ROLE_USER));
    }

    @AfterEach
    void tearUp() {
        userRepo.findByEmail(EMAIL).ifPresent(userRepo::delete);
    }

    private User createUser() {
        User user = new User();
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        user.getRoles().add(Role.ROLE_USER);
        user.getRoles().add(Role.ROLE_ADMIN);
        user.getRoles().add(Role.ROLE_USER_MANAGER);

        return user;
    }
}
