package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ValidationException;
import com.petfeet.service.PetService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin pet listing management: see every listing, approve or reject. */
@WebServlet("/admin/pets")
public class PetApprovalServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("pets", petService.listAll());
            req.setAttribute("statusFilter", param(req, "status"));
            view(req, resp, "admin/pets");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = ValidationUtil.parseId(req.getParameter("id"), "pet id");
            String action = param(req, "action");
            if (!"approve".equals(action) && !"reject".equals(action)) throw new ValidationException("Unknown action.");
            petService.review(user(req), id, "approve".equals(action));
            redirectWithSuccess(req, resp, "Listing " + ("approve".equals(action) ? "approved" : "rejected") + ".", "/admin/pets?status=" + param(req, "status"));
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/admin/pets");
        }
    }
}
