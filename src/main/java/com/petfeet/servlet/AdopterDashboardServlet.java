package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.User;
import com.petfeet.service.AdoptionService;
import com.petfeet.service.AnalyticsService;
import com.petfeet.service.PetService;
import com.petfeet.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Adopter home: statistics, "Pets you may love" and the latest applications. */
@WebServlet("/adopter/dashboard")
public class AdopterDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AnalyticsService analytics = new AnalyticsService();
    private final PetService petService = new PetService();
    private final AdoptionService adoptionService = new AdoptionService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        try {
            User fresh = userService.get(me.getId());       // preferences may have changed
            req.setAttribute("stats", analytics.adopterStats(me.getId()));
            req.setAttribute("recommendations", petService.recommend(fresh, 4));
            List<?> apps = adoptionService.forAdopter(me.getId());
            req.setAttribute("recentApplications", apps.size() > 3 ? apps.subList(0, 3) : apps);
            loadFavorites(req);
            view(req, resp, "adopter/dashboard");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
