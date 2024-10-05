package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.db.FileUser;
import com.mydaytodo.sfa.asset.repository.UserRepositoryImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Service
@Slf4j
@Tag(name ="User Auth Service", description = "Implements UserDetailsService from Spring security")
public class UserAuthServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepositoryImpl repository;
    /**
     * Temporary usage of InMemoryUserDetailsManager, to be replaced with
     * a database (DynamoDB) backed user service.
     */
    private final InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
    public static final UserAuthServiceImpl instance = new UserAuthServiceImpl();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserAuthServiceImpl() {

    }
    private InMemoryUserDetailsManager getInMemoryUserDetailsManager() {
        return inMemoryUserDetailsManager;
    }
    public void addUser(FileUser assetUser) {
        log.info("User object = [ {} ]", assetUser.toString());
        UserDetails userDetails = User.withUsername(assetUser.getUsername())
                        .password(assetUser.getPassword())
                                .roles("ADMIN", "USER")
                                        .build();
        UserAuthServiceImpl.instance.getInMemoryUserDetailsManager().createUser(userDetails);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("In load by username {}", username);
        FileUser user = repository.getUserByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("User with name %s not found", username)));
        log.info("Got the user {}", user.toString());
        UserDetails details;
        if (user.getIsSocialLoginGoogle() != null && user.getIsSocialLoginGoogle()) {
            details = new User(user.getUsername(), "", new ArrayList<>());
        } else {
            details = new User(user.getUsername(), user.getPassword(), new ArrayList<>());
        }
        return details;
    }
}
