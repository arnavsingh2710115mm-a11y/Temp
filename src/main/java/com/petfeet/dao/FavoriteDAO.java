package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;

import java.util.HashSet;
import java.util.Set;

/** Database access for the favorites table (heart button). */
public class FavoriteDAO extends BaseDAO {

    public Set<Integer> petIdsOf(int userId) throws DatabaseException {
        return new HashSet<>(queryList("SELECT pet_id FROM favorites WHERE user_id = ?", rs -> rs.getInt(1), userId));
    }

    /** Adds the favorite if missing, otherwise removes it. Returns true when the pet is now a favorite. */
    public boolean toggle(int userId, int petId) throws DatabaseException {
        if (update("DELETE FROM favorites WHERE user_id = ? AND pet_id = ?", userId, petId) > 0) {
            return false;
        }
        update("INSERT INTO favorites (user_id, pet_id) VALUES (?, ?)", userId, petId);
        return true;
    }

    public long countFor(int userId) throws DatabaseException {
        return count("SELECT COUNT(*) FROM favorites WHERE user_id = ?", userId);
    }
}
