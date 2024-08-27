package com.mydaytodo.sfa.asset.error;

public class EmailAddressException extends RuntimeException  {
    public EmailAddressException() {
        super("Please supply the correct email address");
    }
}
