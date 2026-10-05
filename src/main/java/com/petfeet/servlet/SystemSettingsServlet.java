package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.service.SettingsService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin system settings. Registration switch, approval switch and application limit really change behaviour. */
@WebServlet("/admin/settings")
public class SystemSettingsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final SettingsService settingsService = new SettingsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("settings", settingsService.all());
            view(req, resp, "admin/settings");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            settingsService.save(user(req), req.getParameter("siteName"), req.getParameter("contactEmail"),
                    req.getParameter("allowRegistration") != null, req.getParameter("requirePetApproval") != null,
                    req.getParameter("maxApplications"));
            redirectWithSuccess(req, resp, "Settings saved.", "/admin/settings");
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/admin/settings");
        }
    }
}
