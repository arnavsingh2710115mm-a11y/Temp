package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.service.AnalyticsService;
import com.petfeet.service.PetService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Landing page: hero, featured pets, how it works and the adoption success counter. */
@WebServlet("/home")
public class HomeServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();
    private final AnalyticsService analytics = new AnalyticsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("featuredPets", petService.featured(6));
            req.setAttribute("publicStats", analytics.publicStats());
            loadFavorites(req);
            view(req, resp, "public/home");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
