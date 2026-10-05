package com.petfeet.model;

import java.sql.Timestamp;

/** A pet listed for adoption. Shelter fields are filled when the pet is loaded with a JOIN. */
public class Pet {
    public static final String PENDING = "PENDING";
    public static final String AVAILABLE = "AVAILABLE";
    public static final String ADOPTED = "ADOPTED";
    public static final String REJECTED = "REJECTED";

    private int id;
    private int shelterId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private String gender;
    private String location;
    private String description;
    private String imageUrl;
    private String status = PENDING;
    private Timestamp createdAt;
    private String shelterName;
    private String shelterEmail;
    private String shelterPhone;

    public Pet() { }

    public String getAgeLabel() {
        if (age <= 0) return "Under 1 year";
        return age == 1 ? "1 year" : age + " years";
    }

    public boolean isAvailable() { return AVAILABLE.equals(status); }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getShelterId() { return shelterId; }
    public void setShelterId(int shelterId) { this.shelterId = shelterId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getShelterName() { return shelterName; }
    public void setShelterName(String shelterName) { this.shelterName = shelterName; }
    public String getShelterEmail() { return shelterEmail; }
    public void setShelterEmail(String shelterEmail) { this.shelterEmail = shelterEmail; }
    public String getShelterPhone() { return shelterPhone; }
    public void setShelterPhone(String shelterPhone) { this.shelterPhone = shelterPhone; }
}
