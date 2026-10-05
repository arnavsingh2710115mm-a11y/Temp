package com.petfeet.service;

import com.petfeet.dao.ActivityLogDAO;

/** Best-effort audit trail: a logging failure must never break the real operation. */
public class ActivityService {
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    public void log(Integer userId, String action) {
        try {
            logDAO.log(userId, action);
        } catch (Exception e) {
            System.err.println("[PetFeet] Could not write activity log: " + e.getMessage());
        }
    }
}
