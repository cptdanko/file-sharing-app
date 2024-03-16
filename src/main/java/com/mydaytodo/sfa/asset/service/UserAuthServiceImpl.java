package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.FileUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Data
@Slf4j
public class UserAuthServiceImpl {
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
        log.info(String.format("About to create a user with name [ %s ]", assetUser.getUsername()));
        log.info(String.format("User object = [ %s ]", assetUser.toString()));
        UserDetails userDetails = User.withUsername(assetUser.getUsername())
                        .password(assetUser.getPassword())
                                .roles("ADMIN", "USER")
                                        .build();
        log.info("Added user "+ assetUser.getUsername() + " to InMemoryStore");
        UserAuthServiceImpl.instance.getInMemoryUserDetailsManager().createUser(userDetails);
    }
}
