package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.User;
import com.petfeet.service.AuthService;
import com.petfeet.service.NotificationService;
import com.petfeet.service.QueuedNotificationService;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Public registration. Only ADOPTER and SHELTER can be chosen - the service rejects ADMIN. */
@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AuthService authService = new AuthService();
    private final NotificationService notifier = new QueuedNotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (user(req) != null) {
            WebUtil.redirect(req, resp, user(req).getDashboardPath());
            return;
        }
        req.setAttribute("role", param(req, "role").equals("SHELTER") ? "SHELTER" : "ADOPTER");
        view(req, resp, "public/register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User created = authService.register(req.getParameter("name"), req.getParameter("email"), req.getParameter("phone"),
                    req.getParameter("password"), req.getParameter("confirmPassword"), req.getParameter("role"));
            notifier.notifyUser(created.getId(), "Welcome to PetFeet, " + created.getName() + "! Every paw deserves a home.");
            redirectWithSuccess(req, resp, "Account created! Please log in.", "/login");
        } catch (PetFeetException e) {
            req.setAttribute("name", param(req, "name"));
            req.setAttribute("email", param(req, "email"));
            req.setAttribute("phone", param(req, "phone"));
            req.setAttribute("role", param(req, "role"));
            WebUtil.flashError(req, friendlyMessage(e));
            view(req, resp, "public/register");
        }
    }
}
