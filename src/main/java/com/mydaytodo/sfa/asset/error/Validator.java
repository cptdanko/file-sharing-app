package com.mydaytodo.sfa.asset.error;

import com.mydaytodo.sfa.asset.model.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;
import java.util.regex.Pattern;

@Slf4j
public class Validator {

    public static final String OWASP_PATTERN = "^[a-zA-Z0-9_+&*-] + (?:\\\\.[a-zA-Z0-9_+&*-] + )*@(?:[a-zA-Z0-9-]+\\\\.) + [a-zA-Z]{2, 7}";

    public static void validateUsernameAndToken(String username) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Got user from user context, {}", user.getUsername());
        log.info("The username received, {}", username);
        if(!user.getUsername().equalsIgnoreCase(username)) {
            throw new UsernameMismatchException();
        }
    }

    public static void validateEmailRequest(EmailRequest emailRequest) {
        if(emailRequest.getTo() == null || emailRequest.getTo().isEmpty()) {
            throw new EmailAddressException();
        }

        // find a way to validate the cc and bcc email address
        if(emailRequest.getCc() != null) {
            validateCCBCC(emailRequest.getCc());
        }
        if(emailRequest.getBcc() != null) {
            validateCCBCC(emailRequest.getBcc());
        }
    }
    public static void validateCCBCC(String [] addresses) {
        Arrays.stream(addresses).forEach(emailAdd -> {
            if(!Validator.patternMatches(emailAdd, Validator.OWASP_PATTERN)) {
                throw new EmailAddressException();
            }
        });
    }
    private static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
