package org.paulobichara.prototype.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.paulobichara.prototype.config.DefaultAdminProperties;
import org.paulobichara.prototype.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatabaseInitializerTest {

    @Autowired
    private DefaultAdminProperties adminProperties;

    @Autowired
    private UserRepository userRepo;

    @Test
    void defaultAdminMustBeCreated() {
        assertThat(adminProperties, is(notNullValue()));
        assertThat(adminProperties.getEmail(), is(notNullValue()));
        assertThat(adminProperties.getPassword(), is(notNullValue()));
        assertThat(userRepo.findByEmail(adminProperties.getEmail()).isPresent(), equalTo(true));
    }

}
