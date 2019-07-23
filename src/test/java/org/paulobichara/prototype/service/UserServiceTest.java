package org.paulobichara.prototype.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.exception.UserAlreadyRegisteredException;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepo;

    @Test
    void sameEmailShouldNotBeRegisteredTwice() {
        NewUserDto dto = new NewUserDto();
        dto.setEmail("user@email.com");
        dto.setPassword("user-password");
        User user = userService.addNewUser(dto);
        assertThat(user, is(notNullValue()));
        assertThrows(UserAlreadyRegisteredException.class, () -> userService.addNewUser(dto));
        userRepo.delete(user);
    }

}
