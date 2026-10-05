package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.Role;
import com.petfeet.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Database access for the users table. */
public class UserDAO extends BaseDAO implements CRUDOperations<User, Integer> {

    private static final String COLUMNS = "id, name, email, password, role, phone, address, city, preferred_species, preferred_max_age, is_active, created_at";

    /** Builds the correct subclass (Admin/Shelter/Adopter) for each row - polymorphism in action. */
    private static User map(ResultSet rs) throws SQLException {
        User u = User.create(Role.fromString(rs.getString("role")));
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password"));
        u.setPhone(rs.getString("phone"));
        u.setAddress(rs.getString("address"));
        u.setCity(rs.getString("city"));
        u.setPreferredSpecies(rs.getString("preferred_species"));
        int maxAge = rs.getInt("preferred_max_age");
        u.setPreferredMaxAge(rs.wasNull() ? null : maxAge);
        u.setActive(rs.getBoolean("is_active"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    @Override
    public Integer create(User u) throws DatabaseException {
        return insert("INSERT INTO users (name, email, password, role, phone, address, city, preferred_species, preferred_max_age, is_active) VALUES (?,?,?,?,?,?,?,?,?,?)",
                u.getName(), u.getEmail(), u.getPasswordHash(), u.getRole().name(), u.getPhone(),
                u.getAddress(), u.getCity(), u.getPreferredSpecies(), u.getPreferredMaxAge(), u.isActive());
    }

    @Override
    public User findById(Integer id) throws DatabaseException {
        return queryOne("SELECT " + COLUMNS + " FROM users WHERE id = ?", UserDAO::map, id);
    }

    public User findByEmail(String email) throws DatabaseException {
        return queryOne("SELECT " + COLUMNS + " FROM users WHERE email = ?", UserDAO::map, email);
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        return queryList("SELECT " + COLUMNS + " FROM users ORDER BY id", UserDAO::map);
    }

    public List<User> findByRole(Role role) throws DatabaseException {
        return queryList("SELECT " + COLUMNS + " FROM users WHERE role = ? AND is_active = TRUE ORDER BY name", UserDAO::map, role.name());
    }

    /** Admin update: name, phone, role, active flag (password only when a new hash is supplied). */
    @Override
    public boolean update(User u) throws DatabaseException {
        if (u.getPasswordHash() != null && !u.getPasswordHash().isEmpty()) {
            return update("UPDATE users SET name=?, email=?, phone=?, role=?, is_active=?, password=? WHERE id=?",
                    u.getName(), u.getEmail(), u.getPhone(), u.getRole().name(), u.isActive(), u.getPasswordHash(), u.getId()) > 0;
        }
        return update("UPDATE users SET name=?, email=?, phone=?, role=?, is_active=? WHERE id=?",
                u.getName(), u.getEmail(), u.getPhone(), u.getRole().name(), u.isActive(), u.getId()) > 0;
    }

    /** Self-service profile update. */
    public boolean updateProfile(User u) throws DatabaseException {
        return update("UPDATE users SET name=?, phone=?, address=?, city=?, preferred_species=?, preferred_max_age=? WHERE id=?",
                u.getName(), u.getPhone(), u.getAddress(), u.getCity(), u.getPreferredSpecies(), u.getPreferredMaxAge(), u.getId()) > 0;
    }

    public boolean updatePassword(int userId, String newHash) throws DatabaseException {
        return update("UPDATE users SET password=? WHERE id=?", newHash, userId) > 0;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        return update("DELETE FROM users WHERE id = ?", id) > 0;
    }

    public boolean emailExists(String email, int excludeId) throws DatabaseException {
        return count("SELECT COUNT(*) FROM users WHERE email = ? AND id <> ?", email, excludeId) > 0;
    }

    public long countAll() throws DatabaseException {
        return count("SELECT COUNT(*) FROM users");
    }

    public Map<String, Long> countByRole() throws DatabaseException {
        return groupCount("SELECT role, COUNT(*) FROM users GROUP BY role ORDER BY role");
    }

    public long countActiveShelters() throws DatabaseException {
        return count("SELECT COUNT(DISTINCT u.id) FROM users u WHERE u.role='SHELTER' AND u.is_active = TRUE");
    }

    /** Adopters a shelter may message: people who applied for its pets or already wrote to it. */
    public List<User> findAdoptersForShelter(int shelterId) throws DatabaseException {
        return queryList("SELECT " + COLUMNS + " FROM users WHERE role = 'ADOPTER' AND is_active = TRUE AND ("
                + "id IN (SELECT a.adopter_id FROM adoption_applications a JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ?) "
                + "OR id IN (SELECT sender_id FROM messages WHERE receiver_id = ?)) ORDER BY name", UserDAO::map, shelterId, shelterId);
    }
}
