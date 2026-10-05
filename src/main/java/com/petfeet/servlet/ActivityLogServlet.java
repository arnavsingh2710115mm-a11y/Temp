package com.petfeet.servlet;

import com.petfeet.dao.ActivityLogDAO;
import com.petfeet.exception.PetFeetException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin activity log (latest 200 entries). */
@WebServlet("/admin/logs")
public class ActivityLogServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final ActivityLogDAO logDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("logs", logDAO.recent(200));
            view(req, resp, "admin/logs");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
