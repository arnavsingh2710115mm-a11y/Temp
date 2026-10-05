package com.petfeet.service;

import com.petfeet.dao.ApplicationDAO;
import com.petfeet.dao.FavoriteDAO;
import com.petfeet.dao.PetDAO;
import com.petfeet.exception.AuthenticationException;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ResourceNotFoundException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.AdoptionApplication;
import com.petfeet.model.Pet;
import com.petfeet.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Pet listing rules: CRUD with ownership checks, approval workflow, search, comparison and recommendations. */
public class PetService {
    private final PetDAO petDAO = new PetDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private final SettingsService settings = new SettingsService();
    private final UserService userService = new UserService();
    private final NotificationService notifier = new QueuedNotificationService();
    private final ActivityService activity = new ActivityService();

    /** A pet together with the reason it was recommended. */
    public static class Recommendation {
        private final Pet pet;
        private final int score;
        private final String reason;
        Recommendation(Pet pet, int score, String reason) { this.pet = pet; this.score = score; this.reason = reason; }
        public Pet getPet() { return pet; }
        public int getScore() { return score; }
        public String getReason() { return reason; }
    }

    // ---------- read ----------
    public Pet getPet(int id) throws PetFeetException {
        Pet p = petDAO.findById(id);
        if (p == null) throw new ResourceNotFoundException("We could not find that pet.");
        return p;
    }

    /** Public view: pending / rejected listings are hidden from everyone except the owning shelter and admins. */
    public Pet getVisiblePet(int id, User viewer) throws PetFeetException {
        Pet p = getPet(id);
        boolean publicStatus = Pet.AVAILABLE.equals(p.getStatus()) || Pet.ADOPTED.equals(p.getStatus());
        boolean owner = viewer != null && (viewer.getId() == p.getShelterId() || viewer.getRole().name().equals("ADMIN"));
        if (!publicStatus && !owner) throw new ResourceNotFoundException("We could not find that pet.");
        return p;
    }

    public List<Pet> search(String keyword, String species, String breed, String location, String gender,
                            String maxAgeText, String availability) throws PetFeetException {
        Integer maxAge = null;
        String ageText = com.petfeet.util.ValidationUtil.clean(maxAgeText);
        if (!ageText.isEmpty()) maxAge = com.petfeet.util.ValidationUtil.intInRange(ageText, "Age", 0, 30);
        return petDAO.search(com.petfeet.util.ValidationUtil.clean(keyword), com.petfeet.util.ValidationUtil.clean(species),
                com.petfeet.util.ValidationUtil.clean(breed), com.petfeet.util.ValidationUtil.clean(location),
                com.petfeet.util.ValidationUtil.clean(gender), maxAge, com.petfeet.util.ValidationUtil.clean(availability));
    }

    public List<Pet> featured(int limit) throws PetFeetException {
        List<Pet> available = petDAO.findByStatus(Pet.AVAILABLE);
        return available.size() > limit ? new ArrayList<>(available.subList(0, limit)) : available;
    }

    public List<Pet> listByShelter(int shelterId) throws PetFeetException { return petDAO.findByShelter(shelterId); }
    public List<Pet> listAll() throws PetFeetException { return petDAO.findAll(); }
    public List<String> options(String column) throws PetFeetException { return petDAO.distinctValues(column); }
    public List<String> suggestions(String text) throws PetFeetException {
        String t = com.petfeet.util.ValidationUtil.clean(text);
        return t.length() < 1 ? new ArrayList<>() : petDAO.suggestions(t);
    }

    /** Number of AVAILABLE pets for each shelter id (used on the public Shelters page). */
    public Map<Integer, Long> availableCountByShelter() throws PetFeetException {
        Map<Integer, Long> counts = new HashMap<>();
        for (Pet p : petDAO.findByStatus(Pet.AVAILABLE)) counts.merge(p.getShelterId(), 1L, Long::sum);
        return counts;
    }

    public List<Pet> favoritesOf(int userId) throws PetFeetException {
        return petDAO.findByIds(favoriteDAO.petIdsOf(userId));
    }

    public List<Pet> compare(int firstId, int secondId) throws PetFeetException {
        List<Pet> pair = new ArrayList<>();
        pair.add(getPet(firstId));
        pair.add(getPet(secondId));
        return pair;
    }

    // ---------- create / update / delete ----------
    public Pet add(User shelter, Pet pet) throws PetFeetException {
        validate(pet);
        pet.setShelterId(shelter.getId());
        boolean needsApproval = settings.isTrue("require_pet_approval", true);
        pet.setStatus(needsApproval ? Pet.PENDING : Pet.AVAILABLE);
        pet.setId(petDAO.create(pet));
        activity.log(shelter.getId(), shelter.getName() + " listed pet " + pet.getName());
        if (needsApproval) {
            for (User admin : userService.admins()) {
                notifier.notifyUser(admin.getId(), "Pet listing \"" + pet.getName() + "\" is waiting for approval.");
            }
        }
        return pet;
    }

    public Pet update(User shelter, Pet edited) throws PetFeetException {
        validate(edited);
        Pet existing = getPet(edited.getId());
        requireOwner(shelter, existing);
        edited.setShelterId(existing.getShelterId());
        String requested = edited.getStatus();
        switch (existing.getStatus()) {
            case Pet.PENDING:
                edited.setStatus(Pet.PENDING);
                break;
            case Pet.REJECTED:      // editing a rejected listing re-submits it
                edited.setStatus(settings.isTrue("require_pet_approval", true) ? Pet.PENDING : Pet.AVAILABLE);
                break;
            default:                // approved listing: shelter may switch between AVAILABLE and ADOPTED
                edited.setStatus(Pet.ADOPTED.equals(requested) ? Pet.ADOPTED : Pet.AVAILABLE);
        }
        petDAO.update(edited);
        activity.log(shelter.getId(), shelter.getName() + " updated pet " + edited.getName());
        return edited;
    }

    public void delete(User shelter, int petId) throws PetFeetException {
        Pet existing = getPet(petId);
        requireOwner(shelter, existing);
        petDAO.delete(petId);
        activity.log(shelter.getId(), shelter.getName() + " deleted pet " + existing.getName());
    }

    // ---------- admin approval ----------
    public void review(User admin, int petId, boolean approve) throws PetFeetException {
        Pet pet = getPet(petId);
        petDAO.updateStatus(petId, approve ? Pet.AVAILABLE : Pet.REJECTED);
        notifier.notifyUser(pet.getShelterId(), approve
                ? "Good news! Your listing \"" + pet.getName() + "\" is now live."
                : "Your listing \"" + pet.getName() + "\" was not approved. Edit it and it will be reviewed again.");
        activity.log(admin.getId(), "Admin " + (approve ? "approved" : "rejected") + " pet listing " + pet.getName());
    }

    // ---------- helpers ----------
    private static void requireOwner(User shelter, Pet pet) throws AuthenticationException {
        if (pet.getShelterId() != shelter.getId()) throw new AuthenticationException("You can only manage your own pets.");
    }

    private static void validate(Pet p) throws ValidationException {
        if (p.getName() == null || p.getName().isEmpty()) throw new ValidationException("Pet name is required.");
        if (p.getSpecies() == null || p.getSpecies().isEmpty()) throw new ValidationException("Species is required.");
    }

    // ---------- rule-based recommendations (no external AI) ----------
    /**
     * Scores every available pet against the adopter's profile and behaviour:
     * +3 preferred species, +up to 3 species of favourites/applications, +2 same breed as a liked pet,
     * +2 same city, +1 young enough for the preferred age. Pets already applied for or favourited are skipped.
     */
    public List<Recommendation> recommend(User adopter, int limit) throws PetFeetException {
        Set<Integer> favoriteIds = favoriteDAO.petIdsOf(adopter.getId());
        Set<Integer> appliedIds = new HashSet<>();
        Set<Integer> likedPetIds = new HashSet<>(favoriteIds);
        for (AdoptionApplication a : applicationDAO.findByAdopter(adopter.getId())) {
            appliedIds.add(a.getPetId());
            likedPetIds.add(a.getPetId());
        }
        Map<String, Integer> speciesAffinity = new HashMap<>();
        Set<String> likedBreeds = new HashSet<>();
        for (Pet liked : petDAO.findByIds(likedPetIds)) {
            speciesAffinity.merge(liked.getSpecies(), 1, Integer::sum);
            likedBreeds.add(liked.getBreed().toLowerCase());
        }

        List<Recommendation> scored = new ArrayList<>();
        for (Pet pet : petDAO.findByStatus(Pet.AVAILABLE)) {
            if (appliedIds.contains(pet.getId()) || favoriteIds.contains(pet.getId())) continue;
            int score = 0;
            List<String> reasons = new ArrayList<>();
            if (pet.getSpecies().equalsIgnoreCase(adopter.getPreferredSpecies() == null ? "" : adopter.getPreferredSpecies())) {
                score += 3; reasons.add("you prefer " + pet.getSpecies().toLowerCase() + "s");
            }
            int affinity = Math.min(3, speciesAffinity.getOrDefault(pet.getSpecies(), 0));
            if (affinity > 0) { score += affinity; reasons.add("you liked other " + pet.getSpecies().toLowerCase() + "s"); }
            if (likedBreeds.contains(pet.getBreed().toLowerCase())) { score += 2; reasons.add("same breed as a pet you liked"); }
            if (adopter.getCity() != null && !adopter.getCity().isEmpty() && pet.getLocation().equalsIgnoreCase(adopter.getCity())) {
                score += 2; reasons.add("lives in " + pet.getLocation());
            }
            if (adopter.getPreferredMaxAge() != null && pet.getAge() <= adopter.getPreferredMaxAge()) {
                score += 1; reasons.add("fits your age preference");
            }
            if (score > 0) scored.add(new Recommendation(pet, score, "Because " + String.join(", ", reasons)));
        }
        scored.sort(Comparator.comparingInt(Recommendation::getScore).reversed()
                .thenComparing(r -> r.getPet().getCreatedAt(), Comparator.reverseOrder()));

        if (scored.isEmpty()) {   // new user with no signals: show the newest pets
            for (Pet pet : featured(limit)) {
                if (!appliedIds.contains(pet.getId()) && !favoriteIds.contains(pet.getId())) {
                    scored.add(new Recommendation(pet, 0, "New and waiting for a home"));
                }
            }
        }
        return scored.size() > limit ? new ArrayList<>(scored.subList(0, limit)) : scored;
    }
}
