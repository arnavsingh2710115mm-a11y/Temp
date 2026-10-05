package com.petfeet.model;

import java.sql.Timestamp;

/** One adoption request from an adopter for a pet. */
public class AdoptionApplication {
    public static final String PENDING = "PENDING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";
    public static final String COMPLETED = "COMPLETED";

    private int id;
    private int petId;
    private int adopterId;
    private Timestamp applicationDate;
    private String message;
    private String status = PENDING;
    private String applicantName;
    private String phone;
    private String email;
    private String address;
    private String reason;
    private String experience;
    private String homeType;
    private boolean hasOtherPets;
    // joined data
    private String petName;
    private String petImage;
    private int shelterId;
    private String shelterName;

    /** Index of the current step in the 5-step progress tracker (0 = submitted ... 4 = adopted). */
    public int getProgressStep() {
        switch (status) {
            case APPROVED:  return 3;
            case COMPLETED: return 4;
            case REJECTED:  return 2;
            default:        return 1;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public int getAdopterId() { return adopterId; }
    public void setAdopterId(int adopterId) { this.adopterId = adopterId; }
    public Timestamp getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Timestamp applicationDate) { this.applicationDate = applicationDate; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getHomeType() { return homeType; }
    public void setHomeType(String homeType) { this.homeType = homeType; }
    public boolean isHasOtherPets() { return hasOtherPets; }
    public void setHasOtherPets(boolean hasOtherPets) { this.hasOtherPets = hasOtherPets; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getPetImage() { return petImage; }
    public void setPetImage(String petImage) { this.petImage = petImage; }
    public int getShelterId() { return shelterId; }
    public void setShelterId(int shelterId) { this.shelterId = shelterId; }
    public String getShelterName() { return shelterName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }
}
