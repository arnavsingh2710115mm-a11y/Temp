package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.service.AdoptionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin read-only overview of every adoption application. */
@WebServlet("/admin/applications")
public class AdminApplicationsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("applications", adoptionService.all());
            req.setAttribute("statusFilter", param(req, "status"));
            view(req, resp, "admin/applications");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
