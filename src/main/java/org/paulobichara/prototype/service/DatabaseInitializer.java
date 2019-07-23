package org.paulobichara.prototype.service;

import org.paulobichara.prototype.config.DefaultAdminProperties;
import org.paulobichara.prototype.dto.NewUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Database initializer component responsible for creating the default admin account.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private DefaultAdminProperties defaultAdmin;

    @Override
    public void run(String... strings) {
        if (!userService.checkExistsByEmail(defaultAdmin.getEmail())) {
            NewUserDto dto = new NewUserDto();
            dto.setEmail(defaultAdmin.getEmail());
            dto.setPassword(defaultAdmin.getPassword());
            userService.addNewAdmin(dto);
        }
    }
}