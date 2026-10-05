package com.petfeet.service;

import com.petfeet.dao.AdoptionHistoryDAO;
import com.petfeet.dao.ApplicationDAO;
import com.petfeet.dao.NotificationDAO;
import com.petfeet.dao.PetDAO;
import com.petfeet.exception.AuthenticationException;
import com.petfeet.exception.DatabaseException;
import com.petfeet.exception.DuplicateResourceException;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ResourceNotFoundException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.AdoptionApplication;
import com.petfeet.model.Notification;
import com.petfeet.model.Pet;
import com.petfeet.model.User;
import com.petfeet.util.DBConnection;
import com.petfeet.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/** Adoption workflow. approve() is the JDBC transaction required by the rubric. */
public class AdoptionService {
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final PetDAO petDAO = new PetDAO();
    private final AdoptionHistoryDAO historyDAO = new AdoptionHistoryDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final SettingsService settings = new SettingsService();
    private final NotificationService notifier = new QueuedNotificationService();
    private final ActivityService activity = new ActivityService();

    public AdoptionApplication get(int id) throws PetFeetException {
        AdoptionApplication a = applicationDAO.findById(id);
        if (a == null) throw new ResourceNotFoundException("Application not found.");
        return a;
    }

    public List<AdoptionApplication> forAdopter(int adopterId) throws PetFeetException { return applicationDAO.findByAdopter(adopterId); }
    public List<AdoptionApplication> forShelter(int shelterId) throws PetFeetException { return applicationDAO.findByShelter(shelterId); }
    public List<AdoptionApplication> all() throws PetFeetException { return applicationDAO.findAll(); }

    /** Adopter submits the form. Returns the generated application id. */
    public int apply(User adopter, int petId, String name, String phone, String email, String address, String reason,
                     String experience, String homeType, boolean hasOtherPets, String message) throws PetFeetException {
        AdoptionApplication a = new AdoptionApplication();
        a.setApplicantName(ValidationUtil.required(name, "Applicant name", 100));
        a.setPhone(ValidationUtil.phone(phone));
        a.setEmail(ValidationUtil.email(email));
        a.setAddress(ValidationUtil.required(address, "Address", 255));
        a.setReason(ValidationUtil.required(reason, "Reason for adoption", 1000));
        a.setExperience(ValidationUtil.optional(experience, "Previous pet experience", 255));
        a.setHomeType(ValidationUtil.oneOf(homeType, "home type", "Apartment", "House with garden", "House without garden", "Farm / large property"));
        a.setMessage(ValidationUtil.optional(message, "Message", 1000));
        a.setHasOtherPets(hasOtherPets);

        Pet pet = petDAO.findById(petId);
        if (pet == null) throw new ResourceNotFoundException("We could not find that pet.");
        if (!Pet.AVAILABLE.equals(pet.getStatus())) throw new ValidationException(pet.getName() + " is not available for adoption right now.");
        if (applicationDAO.existsActive(petId, adopter.getId())) {
            throw new DuplicateResourceException("You have already applied for " + pet.getName() + ". Track it under My Applications.");
        }
        int max = settings.getInt("max_active_applications", 5);
        if (applicationDAO.countPendingByAdopter(adopter.getId()) >= max) {
            throw new ValidationException("You already have " + max + " pending applications. Please wait for a response before applying again.");
        }
        a.setPetId(petId);
        a.setAdopterId(adopter.getId());
        a.setStatus(AdoptionApplication.PENDING);
        int id = applicationDAO.create(a);
        notifier.notifyUser(pet.getShelterId(), "New adoption application #" + id + " received for " + pet.getName() + ".");
        activity.log(adopter.getId(), adopter.getName() + " applied to adopt " + pet.getName() + " (application #" + id + ")");
        return id;
    }

    public void reject(User shelter, int applicationId) throws PetFeetException {
        AdoptionApplication a = loadForShelter(shelter, applicationId, AdoptionApplication.PENDING);
        Connection c = DBConnection.getConnection();
        try {
            c.setAutoCommit(false);
            applicationDAO.updateStatus(c, applicationId, AdoptionApplication.REJECTED);
            notificationDAO.create(c, new Notification(a.getAdopterId(), "Your application for " + a.getPetName() + " was not approved this time. Keep looking - there are many pets waiting!"));
            c.commit();
        } catch (PetFeetException | RuntimeException e) {
            rollbackQuietly(c);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(c);
            throw new DatabaseException("Could not reject the application", e);
        } finally {
            closeQuietly(c);
        }
        activity.log(shelter.getId(), shelter.getName() + " rejected application #" + applicationId);
    }

    /**
     * APPROVE - one atomic JDBC transaction:
     *   1. application -> APPROVED
     *   2. pet -> ADOPTED
     *   3. insert adoption_history row
     *   4. notify the adopter (and every other applicant who lost out)
     * If any step fails, rollback() undoes all of them so the data never ends up half-updated.
     */
    public void approve(User shelter, int applicationId) throws PetFeetException {
        AdoptionApplication a = loadForShelter(shelter, applicationId, AdoptionApplication.PENDING);
        Pet pet = petDAO.findById(a.getPetId());
        if (pet == null || !Pet.AVAILABLE.equals(pet.getStatus())) {
            throw new ValidationException("This pet is no longer available, so the application cannot be approved.");
        }
        List<AdoptionApplication> others = applicationDAO.findPendingForPetExcept(a.getPetId(), applicationId);

        Connection c = DBConnection.getConnection();
        try {
            c.setAutoCommit(false);                                                    // start transaction
            applicationDAO.updateStatus(c, applicationId, AdoptionApplication.APPROVED);       // step 1
            petDAO.updateStatus(c, a.getPetId(), Pet.ADOPTED);                                // step 2
            historyDAO.insert(c, applicationId, a.getPetId(), a.getAdopterId(), shelter.getId()); // step 3
            notificationDAO.create(c, new Notification(a.getAdopterId(),
                    "Congratulations! Your application for " + a.getPetName() + " was approved. " + shelter.getName() + " will contact you soon."));   // step 4
            applicationDAO.rejectOtherPending(c, a.getPetId(), applicationId);
            for (AdoptionApplication other : others) {
                notificationDAO.create(c, new Notification(other.getAdopterId(), a.getPetName() + " has found a home with another family. Your application was closed."));
            }
            c.commit();                                                                // all or nothing
        } catch (PetFeetException | RuntimeException e) {
            rollbackQuietly(c);                                                        // undo everything
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(c);
            throw new DatabaseException("Could not approve the application", e);
        } finally {
            closeQuietly(c);
        }
        activity.log(shelter.getId(), shelter.getName() + " approved application #" + applicationId + " for " + a.getPetName());
    }

    /** Shelter marks the hand-over as done: APPROVED -> COMPLETED. */
    public void complete(User shelter, int applicationId) throws PetFeetException {
        AdoptionApplication a = loadForShelter(shelter, applicationId, AdoptionApplication.APPROVED);
        applicationDAO.updateStatus(applicationId, AdoptionApplication.COMPLETED);
        notifier.notifyUser(a.getAdopterId(), "Your adoption of " + a.getPetName() + " is complete. Enjoy your new family member!");
        activity.log(shelter.getId(), shelter.getName() + " completed adoption #" + applicationId);
    }

    private AdoptionApplication loadForShelter(User shelter, int applicationId, String requiredStatus) throws PetFeetException {
        AdoptionApplication a = get(applicationId);
        if (a.getShelterId() != shelter.getId()) throw new AuthenticationException("You can only manage applications for your own pets.");
        if (!requiredStatus.equals(a.getStatus())) throw new ValidationException("This application is already " + a.getStatus().toLowerCase() + ".");
        return a;
    }

    private static void rollbackQuietly(Connection c) {
        try { c.rollback(); } catch (SQLException ignored) { /* connection is closed right after */ }
    }

    private static void closeQuietly(Connection c) {
        try { c.setAutoCommit(true); c.close(); } catch (SQLException ignored) { /* nothing more to do */ }
    }
}
