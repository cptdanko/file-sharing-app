package com.mydaytodo.sfa.asset.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@Slf4j
public class Validator {
    public static void validateUsernameAndToken(String username) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Got user from user context, {}", user.getUsername());
        log.info("The username received, {}", username);
        if(!user.getUsername().equalsIgnoreCase(username)) {
            throw new UsernameMismatchException();
        }
    }
}
