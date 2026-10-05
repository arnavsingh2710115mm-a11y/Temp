package com.petfeet.exception;

/** Thrown when user supplied data is invalid. The message is safe to show to the user. */
public class ValidationException extends PetFeetException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) { super(message); }
}
