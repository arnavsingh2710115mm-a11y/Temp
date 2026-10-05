package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Pet;
import com.petfeet.service.PetService;
import com.petfeet.util.PetFormParser;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** CREATE a pet. New listings wait for admin approval when "require pet approval" is on. */
@WebServlet("/shelter/pets/add")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class AddPetServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("pet", new Pet());
        req.setAttribute("editing", false);
        view(req, resp, "shelter/pet-form");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Pet pet = new Pet();
        try {
            PetFormParser.parse(req, pet, getServletContext().getRealPath("/uploads"));
            Pet saved = petService.add(user(req), pet);
            String note = Pet.PENDING.equals(saved.getStatus())
                    ? saved.getName() + " was saved and is waiting for admin approval."
                    : saved.getName() + " is now listed for adoption!";
            redirectWithSuccess(req, resp, note, "/shelter/pets");
        } catch (PetFeetException e) {
            req.setAttribute("pet", pet);
            req.setAttribute("editing", false);
            WebUtil.flashError(req, friendlyMessage(e));
            view(req, resp, "shelter/pet-form");
        }
    }
}
