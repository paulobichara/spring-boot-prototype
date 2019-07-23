package org.paulobichara.prototype.security;

import org.paulobichara.prototype.model.User;
import org.paulobichara.prototype.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optUser = userRepo.findByEmail(username);
        if (!optUser.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(optUser.get());
    }
}
