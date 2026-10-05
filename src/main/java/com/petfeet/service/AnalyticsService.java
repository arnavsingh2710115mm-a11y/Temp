package com.petfeet.service;

import com.petfeet.dao.AdoptionHistoryDAO;
import com.petfeet.dao.ApplicationDAO;
import com.petfeet.dao.FavoriteDAO;
import com.petfeet.dao.PetDAO;
import com.petfeet.dao.UserDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.model.AdoptionApplication;
import com.petfeet.model.Pet;

import java.util.LinkedHashMap;
import java.util.Map;

/** Builds the numbers shown in the three dashboards. Results are plain Maps so JSP/EL can read them. */
public class AnalyticsService {
    private final UserDAO userDAO = new UserDAO();
    private final PetDAO petDAO = new PetDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final AdoptionHistoryDAO historyDAO = new AdoptionHistoryDAO();
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();

    private static long get(Map<String, Long> map, String key) {
        return map.getOrDefault(key, 0L);
    }

    public Map<String, Object> adminStats() throws PetFeetException {
        Map<String, Object> s = new LinkedHashMap<>();
        Map<String, Long> apps = applicationDAO.countByStatusMap();
        s.put("totalUsers", userDAO.countAll());
        s.put("usersByRole", userDAO.countByRole());
        s.put("totalPets", petDAO.countAll());
        s.put("availablePets", petDAO.countByStatus(Pet.AVAILABLE));
        s.put("adoptedPets", petDAO.countByStatus(Pet.ADOPTED));
        s.put("pendingPets", petDAO.countByStatus(Pet.PENDING));
        s.put("petsByStatus", petDAO.countByStatusMap());
        s.put("petsBySpecies", petDAO.countBySpecies());
        s.put("pendingApplications", get(apps, AdoptionApplication.PENDING));
        s.put("approvedApplications", get(apps, AdoptionApplication.APPROVED) + get(apps, AdoptionApplication.COMPLETED));
        s.put("applicationsByStatus", apps);
        s.put("successfulAdoptions", historyDAO.countAll());
        s.put("activeShelters", userDAO.countActiveShelters());
        return s;
    }

    public Map<String, Object> shelterStats(int shelterId) throws PetFeetException {
        Map<String, Object> s = new LinkedHashMap<>();
        Map<String, Long> apps = applicationDAO.countByStatusForShelter(shelterId);
        long received = apps.values().stream().mapToLong(Long::longValue).sum();
        long approved = get(apps, AdoptionApplication.APPROVED) + get(apps, AdoptionApplication.COMPLETED);
        long listed = petDAO.countByShelter(shelterId);
        long adopted = petDAO.findByShelter(shelterId).stream().filter(p -> Pet.ADOPTED.equals(p.getStatus())).count();
        s.put("listedPets", listed);
        s.put("applicationsReceived", received);
        s.put("pendingApplications", get(apps, AdoptionApplication.PENDING));
        s.put("approvedApplications", approved);
        s.put("adoptedPets", adopted);
        s.put("adoptionRate", listed == 0 ? 0 : Math.round(adopted * 100.0 / listed));
        s.put("applicationsByStatus", apps);
        return s;
    }

    public Map<String, Object> adopterStats(int adopterId) throws PetFeetException {
        Map<String, Object> s = new LinkedHashMap<>();
        Map<String, Long> apps = applicationDAO.countByStatusForAdopter(adopterId);
        long submitted = apps.values().stream().mapToLong(Long::longValue).sum();
        s.put("submitted", submitted);
        s.put("pending", get(apps, AdoptionApplication.PENDING));
        s.put("approved", get(apps, AdoptionApplication.APPROVED) + get(apps, AdoptionApplication.COMPLETED));
        s.put("rejected", get(apps, AdoptionApplication.REJECTED));
        s.put("adoptedPets", historyDAO.findByAdopter(adopterId).size());
        s.put("favorites", favoriteDAO.countFor(adopterId));
        s.put("applicationsByStatus", apps);
        return s;
    }

    /** Public counter shown on the landing page. */
    public Map<String, Object> publicStats() throws PetFeetException {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("adoptions", historyDAO.countAll());
        s.put("availablePets", petDAO.countByStatus(Pet.AVAILABLE));
        s.put("shelters", userDAO.countActiveShelters());
        return s;
    }
}
