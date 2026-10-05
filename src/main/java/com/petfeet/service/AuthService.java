package com.petfeet.service;

import com.petfeet.dao.SettingsDAO;
import com.petfeet.dao.UserDAO;
import com.petfeet.exception.AuthenticationException;
import com.petfeet.exception.DuplicateResourceException;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.Role;
import com.petfeet.model.User;
import com.petfeet.util.PasswordUtil;
import com.petfeet.util.ValidationUtil;

/** Registration and login rules. */
public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final SettingsDAO settingsDAO = new SettingsDAO();
    private final ActivityService activity = new ActivityService();

    public User register(String name, String email, String phone, String password, String confirm, String roleText) throws PetFeetException {
        if (!"true".equals(settingsDAO.get("allow_registration", "true"))) {
            throw new ValidationException("New registrations are currently closed. Please try again later.");
        }
        String cleanName = ValidationUtil.required(name, "Name", 100);
        String cleanEmail = ValidationUtil.email(email);
        String cleanPhone = ValidationUtil.phone(phone);
        ValidationUtil.password(password);
        if (!password.equals(confirm)) throw new ValidationException("Passwords do not match.");
        // Admin accounts can never be created from the public form.
        String role = ValidationUtil.oneOf(roleText, "role", "ADOPTER", "SHELTER");

        if (userDAO.findByEmail(cleanEmail) != null) {
            throw new DuplicateResourceException("This email is already registered. Try logging in instead.");
        }
        User user = User.create(Role.fromString(role));
        user.setName(cleanName);
        user.setEmail(cleanEmail);
        user.setPhone(cleanPhone);
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setId(userDAO.create(user));
        activity.log(user.getId(), "New " + user.getRoleLabel().toLowerCase() + " registered: " + cleanName);
        return user;
    }

    public User login(String email, String password) throws PetFeetException {
        String cleanEmail = ValidationUtil.clean(email).toLowerCase();
        if (cleanEmail.isEmpty() || password == null || password.isEmpty()) {
            throw new ValidationException("Please enter your email and password.");
        }
        User user = userDAO.findByEmail(cleanEmail);
        // same message for unknown email and wrong password -> does not reveal which emails exist
        if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password.");
        }
        if (!user.isActive()) {
            throw new AuthenticationException("This account has been deactivated. Please contact support.");
        }
        activity.log(user.getId(), user.getName() + " logged in");
        return user;
    }

    public User reload(int userId) throws PetFeetException {
        return userDAO.findById(userId);
    }
}
