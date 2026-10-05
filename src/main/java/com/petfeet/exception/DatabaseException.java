package com.petfeet.exception;

/** Wraps a low level SQLException so that the web layer never sees raw JDBC errors. */
public class DatabaseException extends PetFeetException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message, Throwable cause) { super(message, cause); }
}
