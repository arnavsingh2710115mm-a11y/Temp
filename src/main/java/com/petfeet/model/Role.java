package com.petfeet.model;

/** The three kinds of accounts. Stored as text in users.role. */
public enum Role {
    ADMIN, SHELTER, ADOPTER;

    public static Role fromString(String value) {
        return Role.valueOf(value.trim().toUpperCase());
    }
}
