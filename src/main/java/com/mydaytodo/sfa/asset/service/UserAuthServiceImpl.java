package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.FileUser;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Data
@Service
@Slf4j
public class UserAuthServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepositoryImpl repository;
    /**
     * Temporary usage of InMemoryUserDetailsManager, to be replaced with
     * a database (DynamoDB) backed user service.
     */
    private InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
    public static final UserAuthServiceImpl instance = new UserAuthServiceImpl();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserAuthServiceImpl() {

    }
    public void addUser(FileUser assetUser) {
        log.info("About to create a user with name [ {} ]", assetUser.getUsername());
        log.info("User object = [ {} ]", assetUser.toString());
        UserDetails userDetails = User.withUsername(assetUser.getUsername())
                        .password(assetUser.getPassword())
                                .roles("ADMIN", "USER")
                                        .build();
        log.info("Added user {} to InMemoryStore", assetUser.getUsername());
        UserAuthServiceImpl.instance.getInMemoryUserDetailsManager().createUser(userDetails);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("In load by username {}", username);
        if(repository.getUserByUsername(username).isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with name %s not found", username));
        }
        log.info("User exists, so getting it now");
        log.info("Got the optional {}", repository.getUserByUsername(username).isPresent());
        FileUser user = repository.getUserByUsername(username).get();
        log.info("Got the user {}", user.getUsername());
        log.info("Printing out user, {}", user.toString());
        UserDetails details;

        if (user.getIsSocialLoginGoogle() != null) {
            details = new User(user.getUsername(), "", new ArrayList<>());
        } else {
            details = new User(user.getUsername(), user.getPassword(), new ArrayList<>());
        }
        log.info("Successfully initialised {} user details object", details.toString());

        return details;

    }
}
