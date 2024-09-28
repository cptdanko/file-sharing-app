package com.mydaytodo.sfa.asset.error;

public class EntityWithIdNotFoundException extends RuntimeException  {
    public EntityWithIdNotFoundException(String message) {
        super(message);
    }
}
