package com.petfeet.service;

import com.petfeet.dao.SettingsDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.User;
import com.petfeet.util.ValidationUtil;

import java.util.Map;

/** Reads and validates the admin-editable system settings. */
public class SettingsService {
    private final SettingsDAO settingsDAO = new SettingsDAO();
    private final ActivityService activity = new ActivityService();

    public Map<String, String> all() throws PetFeetException { return settingsDAO.findAll(); }

    public boolean isTrue(String key, boolean defaultValue) throws PetFeetException {
        return "true".equals(settingsDAO.get(key, String.valueOf(defaultValue)));
    }

    public int getInt(String key, int defaultValue) throws PetFeetException {
        try {
            return Integer.parseInt(settingsDAO.get(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void save(User admin, String siteName, String contactEmail, boolean allowRegistration,
                     boolean requireApproval, String maxApplications) throws PetFeetException {
        String name = ValidationUtil.required(siteName, "Site name", 60);
        String email = ValidationUtil.email(contactEmail);
        int max = ValidationUtil.intInRange(maxApplications, "Maximum active applications", 1, 50);
        if (max < 1) throw new ValidationException("Maximum active applications must be at least 1.");
        settingsDAO.set("site_name", name);
        settingsDAO.set("contact_email", email);
        settingsDAO.set("allow_registration", String.valueOf(allowRegistration));
        settingsDAO.set("require_pet_approval", String.valueOf(requireApproval));
        settingsDAO.set("max_active_applications", String.valueOf(max));
        activity.log(admin.getId(), "Admin updated system settings");
    }
}
