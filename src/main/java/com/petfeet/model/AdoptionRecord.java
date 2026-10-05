package com.petfeet.model;

import java.sql.Timestamp;

/** A row of the adoption_history table with display data. */
public class AdoptionRecord {
    private int id;
    private int petId;
    private String petName;
    private String petImage;
    private String petBreed;
    private String shelterName;
    private String adopterName;
    private Timestamp adoptedOn;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getPetImage() { return petImage; }
    public void setPetImage(String petImage) { this.petImage = petImage; }
    public String getPetBreed() { return petBreed; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }
    public String getShelterName() { return shelterName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }
    public String getAdopterName() { return adopterName; }
    public void setAdopterName(String adopterName) { this.adopterName = adopterName; }
    public Timestamp getAdoptedOn() { return adoptedOn; }
    public void setAdoptedOn(Timestamp adoptedOn) { this.adoptedOn = adoptedOn; }
}
