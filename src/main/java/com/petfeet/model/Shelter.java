package com.petfeet.model;

import java.util.Arrays;
import java.util.List;

/** Shelter / rescue organisation that lists pets and reviews adoption applications. */
public class Shelter extends User {
    public Shelter() { super(); }
    public Shelter(String name, String email, String phone) { super(name, email, phone); }

    @Override public Role getRole() { return Role.SHELTER; }
    @Override public String getDashboardPath() { return "/shelter/dashboard"; }
    @Override public List<String> getCapabilities() {
        return Arrays.asList("List pets", "Review applications", "Message adopters");
    }
}
