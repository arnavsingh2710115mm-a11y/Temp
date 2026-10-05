package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Key/value system settings that the admin can change at runtime. */
public class SettingsDAO extends BaseDAO {

    public Map<String, String> findAll() throws DatabaseException {
        Map<String, String> settings = new LinkedHashMap<>();
        queryList("SELECT setting_key, setting_value FROM system_settings ORDER BY setting_key",
                rs -> settings.put(rs.getString(1), rs.getString(2)));
        return settings;
    }

    public String get(String key, String defaultValue) throws DatabaseException {
        String v = queryOne("SELECT setting_value FROM system_settings WHERE setting_key = ?", rs -> rs.getString(1), key);
        return v == null ? defaultValue : v;
    }

    public void set(String key, String value) throws DatabaseException {
        if (update("UPDATE system_settings SET setting_value = ? WHERE setting_key = ?", value, key) == 0) {
            update("INSERT INTO system_settings (setting_key, setting_value) VALUES (?, ?)", key, value);
        }
    }
}
