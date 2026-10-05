package com.petfeet.servlet;

import com.petfeet.dao.AdoptionHistoryDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.service.AdoptionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** "My Applications": status list with the progress tracker, plus the adoption history. */
@WebServlet("/adopter/applications")
public class ApplicationStatusServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdoptionService adoptionService = new AdoptionService();
    private final AdoptionHistoryDAO historyDAO = new AdoptionHistoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = user(req).getId();
            req.setAttribute("applications", adoptionService.forAdopter(id));
            req.setAttribute("history", historyDAO.findByAdopter(id));
            view(req, resp, "adopter/applications");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
