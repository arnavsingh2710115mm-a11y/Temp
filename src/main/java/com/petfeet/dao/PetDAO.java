package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.Pet;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Database access for the pets table (always joined with the shelter for display data). */
public class PetDAO extends BaseDAO implements CRUDOperations<Pet, Integer> {

    private static final String SELECT =
            "SELECT p.id, p.shelter_id, p.name, p.species, p.breed, p.age, p.gender, p.location, p.description, p.image_url, p.status, p.created_at, "
          + "u.name AS shelter_name, u.email AS shelter_email, u.phone AS shelter_phone "
          + "FROM pets p JOIN users u ON u.id = p.shelter_id ";

    private static Pet map(ResultSet rs) throws SQLException {
        Pet p = new Pet();
        p.setId(rs.getInt("id"));
        p.setShelterId(rs.getInt("shelter_id"));
        p.setName(rs.getString("name"));
        p.setSpecies(rs.getString("species"));
        p.setBreed(rs.getString("breed"));
        p.setAge(rs.getInt("age"));
        p.setGender(rs.getString("gender"));
        p.setLocation(rs.getString("location"));
        p.setDescription(rs.getString("description"));
        p.setImageUrl(rs.getString("image_url"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setShelterName(rs.getString("shelter_name"));
        p.setShelterEmail(rs.getString("shelter_email"));
        p.setShelterPhone(rs.getString("shelter_phone"));
        return p;
    }

    @Override
    public Integer create(Pet p) throws DatabaseException {
        return insert("INSERT INTO pets (shelter_id, name, species, breed, age, gender, location, description, image_url, status) VALUES (?,?,?,?,?,?,?,?,?,?)",
                p.getShelterId(), p.getName(), p.getSpecies(), p.getBreed(), p.getAge(), p.getGender(),
                p.getLocation(), p.getDescription(), p.getImageUrl(), p.getStatus());
    }

    @Override
    public Pet findById(Integer id) throws DatabaseException {
        return queryOne(SELECT + "WHERE p.id = ?", PetDAO::map, id);
    }

    @Override
    public List<Pet> findAll() throws DatabaseException {
        return queryList(SELECT + "ORDER BY p.created_at DESC", PetDAO::map);
    }

    @Override
    public boolean update(Pet p) throws DatabaseException {
        return update("UPDATE pets SET name=?, species=?, breed=?, age=?, gender=?, location=?, description=?, image_url=?, status=? WHERE id=?",
                p.getName(), p.getSpecies(), p.getBreed(), p.getAge(), p.getGender(), p.getLocation(),
                p.getDescription(), p.getImageUrl(), p.getStatus(), p.getId()) > 0;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        return update("DELETE FROM pets WHERE id = ?", id) > 0;
    }

    public List<Pet> findByShelter(int shelterId) throws DatabaseException {
        return queryList(SELECT + "WHERE p.shelter_id = ? ORDER BY p.created_at DESC", PetDAO::map, shelterId);
    }

    public List<Pet> findByStatus(String status) throws DatabaseException {
        return queryList(SELECT + "WHERE p.status = ? ORDER BY p.created_at DESC", PetDAO::map, status);
    }

    public List<Pet> findByIds(Collection<Integer> ids) throws DatabaseException {
        if (ids.isEmpty()) return new ArrayList<>();
        StringBuilder marks = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) marks.append(i == 0 ? "?" : ",?");
        return queryList(SELECT + "WHERE p.id IN (" + marks + ") ORDER BY p.created_at DESC", PetDAO::map, ids.toArray());
    }

    /**
     * Dynamic search. Only approved listings (AVAILABLE / ADOPTED) are ever visible to the public.
     * Every filter value is bound as a parameter - there is no string concatenation of user input.
     */
    public List<Pet> search(String keyword, String species, String breed, String location, String gender,
                            Integer maxAge, String availability) throws DatabaseException {
        StringBuilder sql = new StringBuilder(SELECT + "WHERE p.status IN ('AVAILABLE','ADOPTED')");
        List<Object> params = new ArrayList<>();
        if (!keyword.isEmpty()) {
            sql.append(" AND (LOWER(p.name) LIKE LOWER(?) OR LOWER(p.breed) LIKE LOWER(?) OR LOWER(p.species) LIKE LOWER(?) OR LOWER(p.description) LIKE LOWER(?))");
            for (int i = 0; i < 4; i++) params.add("%" + keyword + "%");
        }
        if (!species.isEmpty()) { sql.append(" AND p.species = ?"); params.add(species); }
        if (!breed.isEmpty())   { sql.append(" AND LOWER(p.breed) LIKE LOWER(?)"); params.add("%" + breed + "%"); }
        if (!location.isEmpty()){ sql.append(" AND LOWER(p.location) LIKE LOWER(?)"); params.add("%" + location + "%"); }
        if (!gender.isEmpty())  { sql.append(" AND p.gender = ?"); params.add(gender); }
        if (maxAge != null)     { sql.append(" AND p.age <= ?"); params.add(maxAge); }
        if ("AVAILABLE".equals(availability) || "ADOPTED".equals(availability)) {
            sql.append(" AND p.status = ?");
            params.add(availability);
        }
        sql.append(" ORDER BY (p.status = 'AVAILABLE') DESC, p.created_at DESC");
        return queryList(sql.toString(), PetDAO::map, params.toArray());
    }

    public List<String> distinctValues(String column) throws DatabaseException {
        if (!"species".equals(column) && !"breed".equals(column) && !"location".equals(column)) {
            throw new IllegalArgumentException("Unsupported column");
        }
        return queryList("SELECT DISTINCT " + column + " FROM pets WHERE status IN ('AVAILABLE','ADOPTED') ORDER BY " + column, rs -> rs.getString(1));
    }

    /** Auto-complete: pet names, breeds and locations that start with / contain the typed text. */
    public List<String> suggestions(String prefix) throws DatabaseException {
        String like = "%" + prefix + "%";
        return queryList("SELECT value FROM (SELECT name AS value FROM pets WHERE status IN ('AVAILABLE','ADOPTED') AND LOWER(name) LIKE LOWER(?) "
                + "UNION SELECT breed FROM pets WHERE status IN ('AVAILABLE','ADOPTED') AND LOWER(breed) LIKE LOWER(?) "
                + "UNION SELECT location FROM pets WHERE status IN ('AVAILABLE','ADOPTED') AND LOWER(location) LIKE LOWER(?)) s ORDER BY value LIMIT 8",
                rs -> rs.getString(1), like, like, like);
    }

    public boolean updateStatus(Connection c, int petId, String status) throws DatabaseException {
        return update(c, "UPDATE pets SET status = ? WHERE id = ?", status, petId) > 0;
    }

    public boolean updateStatus(int petId, String status) throws DatabaseException {
        return update("UPDATE pets SET status = ? WHERE id = ?", status, petId) > 0;
    }

    public long countAll() throws DatabaseException { return count("SELECT COUNT(*) FROM pets"); }
    public long countByStatus(String status) throws DatabaseException { return count("SELECT COUNT(*) FROM pets WHERE status = ?", status); }
    public long countByShelter(int shelterId) throws DatabaseException { return count("SELECT COUNT(*) FROM pets WHERE shelter_id = ?", shelterId); }

    public Map<String, Long> countBySpecies() throws DatabaseException {
        return groupCount("SELECT species, COUNT(*) FROM pets WHERE status IN ('AVAILABLE','ADOPTED') GROUP BY species ORDER BY 2 DESC");
    }

    public Map<String, Long> countByStatusMap() throws DatabaseException {
        return groupCount("SELECT status, COUNT(*) FROM pets GROUP BY status");
    }
}
