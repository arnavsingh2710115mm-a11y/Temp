package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Pet;
import com.petfeet.service.PetService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** "Find a Pet": search box + filters (species, breed, location, age, gender, availability). */
@WebServlet("/pets")
public class PetSearchServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Pet> pets = petService.search(req.getParameter("q"), req.getParameter("species"), req.getParameter("breed"),
                    req.getParameter("location"), req.getParameter("gender"), req.getParameter("maxAge"), req.getParameter("availability"));
            req.setAttribute("pets", pets);
            req.setAttribute("speciesOptions", petService.options("species"));
            req.setAttribute("locationOptions", petService.options("location"));
            for (String name : new String[]{"q", "species", "breed", "location", "gender", "maxAge", "availability"}) {
                req.setAttribute("f_" + name, param(req, name));
            }
            loadFavorites(req);
            view(req, resp, "public/pets");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
