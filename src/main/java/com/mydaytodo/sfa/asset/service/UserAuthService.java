package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.AssetUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Data
@Slf4j
public class UserAuthService {
    /**
     * Temporary usage of InMemoryUserDetailsManager, to be replaced with
     * a database (DynamoDB) backed user service.
     */
    private InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
    public static final UserAuthService instance = new UserAuthService();
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserAuthService() {

    }
    public void addUser(AssetUser assetUser) {
        log.info("About to create a user");
        log.info(assetUser.toString());
        UserDetails userDetails = User.withUsername(assetUser.getUsername())
                        .password(assetUser.getPassword())
                                .roles("ADMIN", "USER")
                                        .build();
        log.info("Created the user details object to insert into InMemoryStore");
        log.info(userDetails.toString());
        UserAuthService.instance.getInMemoryUserDetailsManager().createUser(userDetails);
    }
}
