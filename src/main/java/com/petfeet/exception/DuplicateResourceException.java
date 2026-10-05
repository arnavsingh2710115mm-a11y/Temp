package com.petfeet.exception;

/** Thrown for duplicates such as an e-mail already registered or an application already submitted. */
public class DuplicateResourceException extends PetFeetException {
    private static final long serialVersionUID = 1L;

    public DuplicateResourceException(String message) { super(message); }
}
