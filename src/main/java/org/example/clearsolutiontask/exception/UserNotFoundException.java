package org.example.clearsolutiontask.exception;

public class UserNotFoundException extends ServiceException {

    public UserNotFoundException(Long id) {
        super("User with id '%s' not found".formatted(id));
    }
}
