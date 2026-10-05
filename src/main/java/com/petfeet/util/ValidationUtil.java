package com.petfeet.util;

import com.petfeet.exception.ValidationException;

import java.util.regex.Pattern;

/** Small reusable server-side validation helpers. */
public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s]{7,15}$");

    private ValidationUtil() { }

    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static String required(String value, String label, int maxLength) throws ValidationException {
        String v = clean(value);
        if (v.isEmpty()) throw new ValidationException(label + " is required.");
        if (v.length() > maxLength) throw new ValidationException(label + " must be at most " + maxLength + " characters.");
        return v;
    }

    public static String optional(String value, String label, int maxLength) throws ValidationException {
        String v = clean(value);
        if (v.length() > maxLength) throw new ValidationException(label + " must be at most " + maxLength + " characters.");
        return v;
    }

    public static String email(String value) throws ValidationException {
        String v = required(value, "Email", 150).toLowerCase();
        if (!EMAIL.matcher(v).matches()) throw new ValidationException("Please enter a valid email address.");
        return v;
    }

    public static String phone(String value) throws ValidationException {
        String v = required(value, "Phone", 20);
        if (!PHONE.matcher(v).matches()) throw new ValidationException("Phone must contain 7-15 digits (you may use + - and spaces).");
        return v;
    }

    public static String password(String value) throws ValidationException {
        if (value == null || value.length() < 8) throw new ValidationException("Password must be at least 8 characters long.");
        boolean letter = value.chars().anyMatch(Character::isLetter);
        boolean digit = value.chars().anyMatch(Character::isDigit);
        if (!letter || !digit) throw new ValidationException("Password must contain at least one letter and one number.");
        return value;
    }

    public static int intInRange(String value, String label, int min, int max) throws ValidationException {
        try {
            int n = Integer.parseInt(clean(value));
            if (n < min || n > max) throw new ValidationException(label + " must be between " + min + " and " + max + ".");
            return n;
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " must be a number.");
        }
    }

    public static int parseId(String value, String label) throws ValidationException {
        try {
            return Integer.parseInt(clean(value));
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid " + label + ".");
        }
    }

    /** Value must be one of the allowed options (case-sensitive). */
    public static String oneOf(String value, String label, String... allowed) throws ValidationException {
        String v = clean(value);
        for (String a : allowed) if (a.equals(v)) return v;
        throw new ValidationException("Invalid " + label + ".");
    }
}
