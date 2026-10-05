package com.petfeet.service;

import com.petfeet.dao.UserDAO;
import com.petfeet.exception.AuthenticationException;
import com.petfeet.exception.DuplicateResourceException;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ResourceNotFoundException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.Role;
import com.petfeet.model.User;
import com.petfeet.util.PasswordUtil;
import com.petfeet.util.ValidationUtil;

import java.util.List;

/** Admin user management (CRUD) and self-service profile updates. */
public class UserService {
    private final UserDAO userDAO = new UserDAO();
    private final ActivityService activity = new ActivityService();

    public List<User> listAll() throws PetFeetException { return userDAO.findAll(); }

    public User get(int id) throws PetFeetException {
        User u = userDAO.findById(id);
        if (u == null) throw new ResourceNotFoundException("User not found.");
        return u;
    }

    public List<User> adoptersForShelter(int shelterId) throws PetFeetException { return userDAO.findAdoptersForShelter(shelterId); }
    public List<User> shelters() throws PetFeetException { return userDAO.findByRole(Role.SHELTER); }
    public List<User> admins() throws PetFeetException { return userDAO.findByRole(Role.ADMIN); }

    /** CREATE - admin adds any kind of user, including other admins. */
    public User create(User admin, String name, String email, String phone, String password, String role, boolean active) throws PetFeetException {
        User u = User.create(Role.fromString(ValidationUtil.oneOf(role, "role", "ADMIN", "SHELTER", "ADOPTER")));
        u.setName(ValidationUtil.required(name, "Name", 100));
        u.setEmail(ValidationUtil.email(email));
        u.setPhone(ValidationUtil.phone(phone));
        ValidationUtil.password(password);
        u.setActive(active);
        if (userDAO.findByEmail(u.getEmail()) != null) throw new DuplicateResourceException("This email is already registered.");
        u.setPasswordHash(PasswordUtil.hash(password));
        u.setId(userDAO.create(u));
        activity.log(admin.getId(), "Admin created user " + u.getEmail() + " (" + u.getRole() + ")");
        return u;
    }

    /** UPDATE - an empty password means "keep the current one". */
    public void update(User admin, int id, String name, String email, String phone, String newPassword, String role, boolean active) throws PetFeetException {
        User u = get(id);
        u.setName(ValidationUtil.required(name, "Name", 100));
        u.setEmail(ValidationUtil.email(email));
        u.setPhone(ValidationUtil.phone(phone));
        Role newRole = Role.fromString(ValidationUtil.oneOf(role, "role", "ADMIN", "SHELTER", "ADOPTER"));
        if (id == admin.getId() && (newRole != Role.ADMIN || !active)) {
            throw new ValidationException("You cannot remove your own admin access or deactivate yourself.");
        }
        if (userDAO.emailExists(u.getEmail(), id)) throw new DuplicateResourceException("This email is already used by another account.");
        User updated = User.create(newRole);          // role change => different subclass instance
        updated.setId(id);
        updated.setName(u.getName());
        updated.setEmail(u.getEmail());
        updated.setPhone(u.getPhone());
        updated.setActive(active);
        if (newPassword != null && !newPassword.isEmpty()) {
            ValidationUtil.password(newPassword);
            updated.setPasswordHash(PasswordUtil.hash(newPassword));
        }
        userDAO.update(updated);
        activity.log(admin.getId(), "Admin updated user " + u.getEmail());
    }

    /** DELETE */
    public void delete(User admin, int id) throws PetFeetException {
        if (id == admin.getId()) throw new ValidationException("You cannot delete your own account.");
        User u = get(id);
        userDAO.delete(id);
        activity.log(admin.getId(), "Admin deleted user " + u.getEmail());
    }

    public void toggleActive(User admin, int id) throws PetFeetException {
        User u = get(id);
        if (id == admin.getId()) throw new ValidationException("You cannot deactivate your own account.");
        u.setActive(!u.isActive());
        userDAO.update(u);
        activity.log(admin.getId(), (u.isActive() ? "Activated " : "Deactivated ") + u.getEmail());
    }

    /** Profile update for the logged-in user. Password is changed only when a new one is typed. */
    public User updateProfile(User current, String name, String phone, String address, String city,
                              String preferredSpecies, String maxAge, String currentPassword, String newPassword) throws PetFeetException {
        User u = get(current.getId());
        u.setName(ValidationUtil.required(name, "Name", 100));
        u.setPhone(ValidationUtil.phone(phone));
        u.setAddress(ValidationUtil.optional(address, "Address", 255));
        u.setCity(ValidationUtil.optional(city, "City", 80));
        String species = ValidationUtil.optional(preferredSpecies, "Preferred species", 40);
        u.setPreferredSpecies(species.isEmpty() ? null : species);
        String age = ValidationUtil.clean(maxAge);
        u.setPreferredMaxAge(age.isEmpty() ? null : ValidationUtil.intInRange(age, "Preferred maximum age", 0, 30));
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!PasswordUtil.verify(currentPassword, u.getPasswordHash())) {
                throw new AuthenticationException("Your current password is not correct.");
            }
            ValidationUtil.password(newPassword);
            userDAO.updatePassword(u.getId(), PasswordUtil.hash(newPassword));
        }
        userDAO.updateProfile(u);
        activity.log(u.getId(), u.getName() + " updated their profile");
        return userDAO.findById(u.getId());
    }
}
