package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.User;
import com.petfeet.service.AdoptionService;
import com.petfeet.service.AnalyticsService;
import com.petfeet.service.PetService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** /shelter/dashboard = statistics overview, /shelter/pets = full list of the shelter's pet cards. */
@WebServlet({"/shelter/dashboard", "/shelter/pets"})
public class ShelterDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AnalyticsService analytics = new AnalyticsService();
    private final PetService petService = new PetService();
    private final AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        try {
            req.setAttribute("pets", petService.listByShelter(me.getId()));
            if ("/shelter/pets".equals(req.getServletPath())) {
                view(req, resp, "shelter/pets");
                return;
            }
            req.setAttribute("stats", analytics.shelterStats(me.getId()));
            List<?> apps = adoptionService.forShelter(me.getId());
            req.setAttribute("recentApplications", apps.size() > 4 ? apps.subList(0, 4) : apps);
            view(req, resp, "shelter/dashboard");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
