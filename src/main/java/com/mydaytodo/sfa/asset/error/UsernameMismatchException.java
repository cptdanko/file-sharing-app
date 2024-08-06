package com.mydaytodo.sfa.asset.error;

public class UsernameMismatchException extends RuntimeException {

    public UsernameMismatchException() {
        super("The username supplied doesn't match token");
    }
}
