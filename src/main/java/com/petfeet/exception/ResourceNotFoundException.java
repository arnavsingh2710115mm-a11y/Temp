package com.petfeet.exception;

/** Thrown when a pet, user or application cannot be found. */
public class ResourceNotFoundException extends PetFeetException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) { super(message); }
}
