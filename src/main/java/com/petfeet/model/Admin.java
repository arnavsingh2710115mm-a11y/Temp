package com.petfeet.model;

import java.util.Arrays;
import java.util.List;

/** Platform administrator: manages users, approves listings and system settings. */
public class Admin extends User {
    @Override public Role getRole() { return Role.ADMIN; }
    @Override public String getDashboardPath() { return "/admin/dashboard"; }
    @Override public List<String> getCapabilities() {
        return Arrays.asList("Manage users", "Approve pet listings", "View all applications", "System settings", "Analytics");
    }
}
