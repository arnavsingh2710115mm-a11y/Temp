package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.User;
import com.petfeet.service.UserService;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** View and update the logged-in user's profile (all three roles). */
@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("profile", userService.get(user(req).getId()));
            view(req, resp, "common/profile");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User updated = userService.updateProfile(user(req), req.getParameter("name"), req.getParameter("phone"),
                    req.getParameter("address"), req.getParameter("city"), req.getParameter("preferredSpecies"),
                    req.getParameter("preferredMaxAge"), req.getParameter("currentPassword"), req.getParameter("newPassword"));
            req.getSession().setAttribute(WebUtil.SESSION_USER, updated);     // keep the session copy fresh
            redirectWithSuccess(req, resp, "Profile updated successfully.", "/profile");
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/profile");
        }
    }
}
