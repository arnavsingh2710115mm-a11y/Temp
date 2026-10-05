package com.petfeet.model;

import java.util.Arrays;
import java.util.List;

/** Person looking to adopt a pet. */
public class Adopter extends User {
    public Adopter() { super(); }
    public Adopter(String name, String email, String phone) { super(name, email, phone); }

    @Override public Role getRole() { return Role.ADOPTER; }
    @Override public String getDashboardPath() { return "/adopter/dashboard"; }
    @Override public List<String> getCapabilities() {
        return Arrays.asList("Browse and search pets", "Apply for adoption", "Track applications", "Message shelters");
    }
}
