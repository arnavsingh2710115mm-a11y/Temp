package com.petfeet.exception;

/** Base class of all application-specific (checked) exceptions. */
public class PetFeetException extends Exception {
    private static final long serialVersionUID = 1L;

    public PetFeetException(String message) { super(message); }
    public PetFeetException(String message, Throwable cause) { super(message, cause); }
}
