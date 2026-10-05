package com.petfeet.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Abstract base class for every account in PetFeet.
 * Demonstrates ABSTRACTION (abstract methods), ENCAPSULATION (private fields + getters/setters)
 * and INHERITANCE (Admin, Shelter and Adopter extend this class).
 */
public abstract class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String phone;
    private String address;
    private String city;
    private String preferredSpecies;
    private Integer preferredMaxAge;
    private boolean active = true;
    private Timestamp createdAt;

    protected User() { }

    protected User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // ----- abstract behaviour implemented differently by each role (polymorphism) -----
    public abstract Role getRole();
    public abstract String getDashboardPath();
    public abstract List<String> getCapabilities();

    /** Factory that returns the right subclass for a role. */
    public static User create(Role role) {
        switch (role) {
            case ADMIN:   return new Admin();
            case SHELTER: return new Shelter();
            default:      return new Adopter();
        }
    }

    public String getRoleLabel() {
        String r = getRole().name();
        return r.charAt(0) + r.substring(1).toLowerCase();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPreferredSpecies() { return preferredSpecies; }
    public void setPreferredSpecies(String preferredSpecies) { this.preferredSpecies = preferredSpecies; }
    public Integer getPreferredMaxAge() { return preferredMaxAge; }
    public void setPreferredMaxAge(Integer preferredMaxAge) { this.preferredMaxAge = preferredMaxAge; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", email=" + email + "]";
    }
}
