package com.mydaytodo.sfa.asset.error;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class Validator {
    public static void validateUsernameAndToken(String username) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.getUsername().equalsIgnoreCase(username)) {
            throw new UsernameMismatchException();
        }
    }
}
