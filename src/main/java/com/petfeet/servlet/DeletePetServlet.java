package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.service.PetService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** DELETE a pet (POST only, protected by the CSRF filter). */
@WebServlet("/shelter/pets/delete")
public class DeletePetServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            petService.delete(user(req), ValidationUtil.parseId(req.getParameter("id"), "pet id"));
            redirectWithSuccess(req, resp, "The pet listing was deleted.", "/shelter/pets");
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/shelter/pets");
        }
    }
}
