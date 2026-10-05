package com.petfeet.servlet;

import com.petfeet.dao.FavoriteDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Role;
import com.petfeet.model.User;
import com.petfeet.service.PetService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * GET  /adopter/favorites         -> the adopter's favourite pets page.
 * POST /adopter/favorites         -> heart toggle (called with fetch() from the heart button, answers JSON).
 */
@WebServlet({"/adopter/favorites", "/favorite"})
public class FavoriteServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final FavoriteDAO favoriteDAO = new FavoriteDAO();
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("pets", petService.favoritesOf(user(req).getId()));
            loadFavorites(req);
            view(req, resp, "adopter/favorites");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        User me = user(req);
        if (me == null || me.getRole() != Role.ADOPTER) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Log in as an adopter to save favourites.\"}");
            return;
        }
        try {
            int petId = ValidationUtil.parseId(req.getParameter("petId"), "pet id");
            petService.getVisiblePet(petId, me);                   // must be a visible pet
            boolean nowFavorite = favoriteDAO.toggle(me.getId(), petId);
            resp.getWriter().write("{\"favorite\":" + nowFavorite + "}");
        } catch (PetFeetException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":" + com.petfeet.util.WebUtil.jsonString(friendlyMessage(e)) + "}");
        }
    }
}
