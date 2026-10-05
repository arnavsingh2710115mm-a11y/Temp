package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Pet;
import com.petfeet.service.PetService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Large pet profile page with shelter information and the "Apply for Adoption" button. */
@WebServlet("/pet")
public class PetDetailsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = ValidationUtil.parseId(req.getParameter("id"), "pet id");
            Pet pet = petService.getVisiblePet(id, user(req));
            req.setAttribute("pet", pet);
            loadFavorites(req);
            view(req, resp, "public/pet-details");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
