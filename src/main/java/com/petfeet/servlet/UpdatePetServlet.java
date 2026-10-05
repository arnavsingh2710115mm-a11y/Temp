package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Pet;
import com.petfeet.service.PetService;
import com.petfeet.util.PetFormParser;
import com.petfeet.util.ValidationUtil;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** UPDATE a pet (details, photo and adoption status). Only the owning shelter may edit. */
@WebServlet("/shelter/pets/edit")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class UpdatePetServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Pet pet = petService.getPet(ValidationUtil.parseId(req.getParameter("id"), "pet id"));
            if (pet.getShelterId() != user(req).getId()) {
                throw new com.petfeet.exception.AuthenticationException("You can only edit your own pets.");
            }
            req.setAttribute("pet", pet);
            req.setAttribute("editing", true);
            view(req, resp, "shelter/pet-form");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Pet pet = new Pet();
        try {
            int id = ValidationUtil.parseId(req.getParameter("id"), "pet id");
            Pet existing = petService.getPet(id);
            pet.setId(id);
            pet.setImageUrl(existing.getImageUrl());          // keep the current photo unless a new one is supplied
            PetFormParser.parse(req, pet, getServletContext().getRealPath("/uploads"));
            petService.update(user(req), pet);
            redirectWithSuccess(req, resp, pet.getName() + " was updated.", "/shelter/pets");
        } catch (PetFeetException e) {
            req.setAttribute("pet", pet);
            req.setAttribute("editing", true);
            WebUtil.flashError(req, friendlyMessage(e));
            view(req, resp, "shelter/pet-form");
        }
    }
}
