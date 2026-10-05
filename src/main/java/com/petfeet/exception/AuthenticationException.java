package com.petfeet.exception;

/** Thrown for failed logins and for operations the current user is not allowed to perform. */
public class AuthenticationException extends PetFeetException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException(String message) { super(message); }
}
