package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.AdoptionApplication;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** Database access for adoption_applications. */
public class ApplicationDAO extends BaseDAO implements CRUDOperations<AdoptionApplication, Integer> {

    private static final String SELECT =
            "SELECT a.*, p.name AS pet_name, p.image_url AS pet_image, p.shelter_id AS shelter_id, s.name AS shelter_name "
          + "FROM adoption_applications a JOIN pets p ON p.id = a.pet_id JOIN users s ON s.id = p.shelter_id ";

    private static AdoptionApplication map(ResultSet rs) throws SQLException {
        AdoptionApplication a = new AdoptionApplication();
        a.setId(rs.getInt("id"));
        a.setPetId(rs.getInt("pet_id"));
        a.setAdopterId(rs.getInt("adopter_id"));
        a.setApplicationDate(rs.getTimestamp("application_date"));
        a.setMessage(rs.getString("message"));
        a.setStatus(rs.getString("status"));
        a.setApplicantName(rs.getString("applicant_name"));
        a.setPhone(rs.getString("phone"));
        a.setEmail(rs.getString("email"));
        a.setAddress(rs.getString("address"));
        a.setReason(rs.getString("reason"));
        a.setExperience(rs.getString("experience"));
        a.setHomeType(rs.getString("home_type"));
        a.setHasOtherPets(rs.getBoolean("has_other_pets"));
        a.setPetName(rs.getString("pet_name"));
        a.setPetImage(rs.getString("pet_image"));
        a.setShelterId(rs.getInt("shelter_id"));
        a.setShelterName(rs.getString("shelter_name"));
        return a;
    }

    @Override
    public Integer create(AdoptionApplication a) throws DatabaseException {
        return insert("INSERT INTO adoption_applications (pet_id, adopter_id, message, status, applicant_name, phone, email, address, reason, experience, home_type, has_other_pets) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                a.getPetId(), a.getAdopterId(), a.getMessage(), a.getStatus(), a.getApplicantName(), a.getPhone(),
                a.getEmail(), a.getAddress(), a.getReason(), a.getExperience(), a.getHomeType(), a.isHasOtherPets());
    }

    @Override
    public AdoptionApplication findById(Integer id) throws DatabaseException {
        return queryOne(SELECT + "WHERE a.id = ?", ApplicationDAO::map, id);
    }

    @Override
    public List<AdoptionApplication> findAll() throws DatabaseException {
        return queryList(SELECT + "ORDER BY a.application_date DESC", ApplicationDAO::map);
    }

    /** Only the applicant-editable text fields can be changed after submission. */
    @Override
    public boolean update(AdoptionApplication a) throws DatabaseException {
        return update("UPDATE adoption_applications SET message = ?, status = ? WHERE id = ?", a.getMessage(), a.getStatus(), a.getId()) > 0;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        return update("DELETE FROM adoption_applications WHERE id = ?", id) > 0;
    }

    public List<AdoptionApplication> findByAdopter(int adopterId) throws DatabaseException {
        return queryList(SELECT + "WHERE a.adopter_id = ? ORDER BY a.application_date DESC", ApplicationDAO::map, adopterId);
    }

    public List<AdoptionApplication> findByShelter(int shelterId) throws DatabaseException {
        return queryList(SELECT + "WHERE p.shelter_id = ? ORDER BY (a.status = 'PENDING') DESC, a.application_date DESC", ApplicationDAO::map, shelterId);
    }

    public List<AdoptionApplication> findPendingForPetExcept(int petId, int exceptApplicationId) throws DatabaseException {
        return queryList(SELECT + "WHERE a.pet_id = ? AND a.status = 'PENDING' AND a.id <> ?", ApplicationDAO::map, petId, exceptApplicationId);
    }

    /** True if the adopter already has a live (pending / approved / completed) application for this pet. */
    public boolean existsActive(int petId, int adopterId) throws DatabaseException {
        return count("SELECT COUNT(*) FROM adoption_applications WHERE pet_id = ? AND adopter_id = ? AND status <> 'REJECTED'", petId, adopterId) > 0;
    }

    public long countPendingByAdopter(int adopterId) throws DatabaseException {
        return count("SELECT COUNT(*) FROM adoption_applications WHERE adopter_id = ? AND status = 'PENDING'", adopterId);
    }

    public boolean updateStatus(Connection c, int applicationId, String status) throws DatabaseException {
        return update(c, "UPDATE adoption_applications SET status = ? WHERE id = ?", status, applicationId) > 0;
    }

    public boolean updateStatus(int applicationId, String status) throws DatabaseException {
        return update("UPDATE adoption_applications SET status = ? WHERE id = ?", status, applicationId) > 0;
    }

    public int rejectOtherPending(Connection c, int petId, int exceptApplicationId) throws DatabaseException {
        return update(c, "UPDATE adoption_applications SET status = 'REJECTED' WHERE pet_id = ? AND status = 'PENDING' AND id <> ?", petId, exceptApplicationId);
    }

    // ----- statistics -----
    public long countByStatus(String status) throws DatabaseException {
        return count("SELECT COUNT(*) FROM adoption_applications WHERE status = ?", status);
    }

    public Map<String, Long> countByStatusMap() throws DatabaseException {
        return groupCount("SELECT status, COUNT(*) FROM adoption_applications GROUP BY status");
    }

    public Map<String, Long> countByStatusForShelter(int shelterId) throws DatabaseException {
        return groupCount("SELECT a.status, COUNT(*) FROM adoption_applications a JOIN pets p ON p.id = a.pet_id WHERE p.shelter_id = ? GROUP BY a.status", shelterId);
    }

    public Map<String, Long> countByStatusForAdopter(int adopterId) throws DatabaseException {
        return groupCount("SELECT status, COUNT(*) FROM adoption_applications WHERE adopter_id = ? GROUP BY status", adopterId);
    }
}
