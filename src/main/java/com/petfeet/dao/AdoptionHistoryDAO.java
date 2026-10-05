package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.AdoptionRecord;

import java.sql.Connection;
import java.util.List;

/** Database access for adoption_history (rows are written inside the approval transaction). */
public class AdoptionHistoryDAO extends BaseDAO {

    private static final String SELECT =
            "SELECT h.id, h.pet_id, h.adopted_on, p.name AS pet_name, p.image_url, p.breed, s.name AS shelter_name, u.name AS adopter_name "
          + "FROM adoption_history h JOIN pets p ON p.id = h.pet_id JOIN users s ON s.id = h.shelter_id JOIN users u ON u.id = h.adopter_id ";

    public int insert(Connection c, int applicationId, int petId, int adopterId, int shelterId) throws DatabaseException {
        return insert(c, "INSERT INTO adoption_history (application_id, pet_id, adopter_id, shelter_id) VALUES (?,?,?,?)",
                applicationId, petId, adopterId, shelterId);
    }

    public List<AdoptionRecord> findByAdopter(int adopterId) throws DatabaseException {
        return queryList(SELECT + "WHERE h.adopter_id = ? ORDER BY h.adopted_on DESC", rs -> {
            AdoptionRecord r = new AdoptionRecord();
            r.setId(rs.getInt("id"));
            r.setPetId(rs.getInt("pet_id"));
            r.setPetName(rs.getString("pet_name"));
            r.setPetImage(rs.getString("image_url"));
            r.setPetBreed(rs.getString("breed"));
            r.setShelterName(rs.getString("shelter_name"));
            r.setAdopterName(rs.getString("adopter_name"));
            r.setAdoptedOn(rs.getTimestamp("adopted_on"));
            return r;
        }, adopterId);
    }

    public long countAll() throws DatabaseException {
        return count("SELECT COUNT(*) FROM adoption_history");
    }
}
