package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.User;
import com.petfeet.service.AdoptionService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Shelter reviews applications: approve (transaction), reject, or mark the hand-over completed. */
@WebServlet("/shelter/applications")
public class ApplicationManagementServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("applications", adoptionService.forShelter(user(req).getId()));
            view(req, resp, "shelter/applications");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        try {
            int id = ValidationUtil.parseId(req.getParameter("id"), "application");
            switch (param(req, "action")) {
                case "approve":
                    adoptionService.approve(me, id);
                    redirectWithSuccess(req, resp, "Application #" + id + " approved. The pet is now marked as adopted.", "/shelter/applications");
                    return;
                case "reject":
                    adoptionService.reject(me, id);
                    redirectWithSuccess(req, resp, "Application #" + id + " was rejected.", "/shelter/applications");
                    return;
                case "complete":
                    adoptionService.complete(me, id);
                    redirectWithSuccess(req, resp, "Adoption #" + id + " marked as completed.", "/shelter/applications");
                    return;
                default:
                    throw new ValidationException("Unknown action.");
            }
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/shelter/applications");
        }
    }
}
