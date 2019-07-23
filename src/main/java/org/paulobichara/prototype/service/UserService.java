package org.paulobichara.prototype.service;

import org.paulobichara.prototype.dto.NewUserDto;
import org.paulobichara.prototype.dto.UpdateUserDto;
import org.paulobichara.prototype.exception.UserAlreadyRegisteredException;
import org.paulobichara.prototype.model.Role;
import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.repository.UserRepository;
import org.paulobichara.prototype.exception.UserNotFoundException;
import org.paulobichara.prototype.security.annotation.IsAdmin;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Creates a new user.
     * @param dto user registration information
     * @return persisted {@link User} instance
     */
    public User addNewUser(@Valid NewUserDto dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyRegisteredException(dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.getRoles().add(Role.ROLE_USER);

        return userRepo.save(user);
    }

    /**
     * Creates a new admin. Admins have {@link Role} ROLE_ADMIN and can access and modify data from all users.
     * @param dto admin registration information
     * @return persisted admin {@link User} instance
     */
    @IsAdmin
    public User addNewAdmin(@Valid NewUserDto dto) {
        return userRepo.save(createNewAdminUser(dto, Role.ROLE_ADMIN));
    }

    /**
     * Creates a new user manager. User managers have {@link Role} ROLE_USER_MANAGER and can access and modify only
     * user records.
     * @param dto user manager registration information
     * @return persisted user manager {@link User} instance
     */
    @IsAdmin
    public User addNewUserManager(@Valid NewUserDto dto) {
        return userRepo.save(createNewAdminUser(dto, Role.ROLE_USER_MANAGER));
    }

    /**
     * Searches for users based on the user id and the specification parsed from the search query.
     *
     * @param spec specification parsed from the search query
     * @param pageable pagination data
     * @return list of filtered users
     */
    @IsAdmin
    public List<User> searchUsers(Specification<User> spec, Pageable pageable) {
        return spec == null ? userRepo.findAll(pageable).getContent() : userRepo.findAll(spec, pageable).getContent();
    }

    /**
     * Finds user by id.
     * @param userId id of the user
     * @return found {@link User} instance
     */
    public User findUserById(@NotNull Long userId) {
        Optional<User> optional = userRepo.findById(userId);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new UserNotFoundException(userId);
    }

    boolean checkExistsByEmail(@NotNull String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    /**
     * Updates user by id.
     * @param userId is of the user to be updated
     * @param dto user update information
     * @return updated {@link User} instance
     */
    public User updateUserById(@NotNull Long userId, @Valid UpdateUserDto dto) {
        User user = findUserById(userId);
        boolean changed = false;

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            changed = true;
        }

        if (changed) {
            user = userRepo.save(user);
        }

        return user;
    }

    /**
     * Removes user by id.
     * @param userId id of the user to be removed
     * @return removed {@link User} instance
     */
    public User removeUser(@NotNull Long userId) {
        Optional<User> optional = userRepo.findById(userId);
        if (optional.isPresent()) {
            userRepo.delete(optional.get());
            return optional.get();
        }
        throw new UserNotFoundException(userId);
    }

    private User createNewAdminUser(NewUserDto dto, Role role) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.getRoles().add(role);
        return user;
    }
}
