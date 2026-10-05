package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.service.AnalyticsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin analytics page with JavaScript charts. */
@WebServlet("/admin/analytics")
public class AnalyticsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AnalyticsService analytics = new AnalyticsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("stats", analytics.adminStats());
            view(req, resp, "admin/analytics");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
