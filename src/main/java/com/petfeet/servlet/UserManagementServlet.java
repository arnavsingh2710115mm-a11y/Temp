package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.User;
import com.petfeet.service.UserService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin CRUD for users: list (READ), create, update, delete and activate / deactivate. */
@WebServlet("/admin/users")
public class UserManagementServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("users", userService.listAll());
            req.setAttribute("roleFilter", param(req, "role"));
            view(req, resp, "admin/users");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = user(req);
        try {
            switch (param(req, "action")) {
                case "create":
                    userService.create(admin, req.getParameter("name"), req.getParameter("email"), req.getParameter("phone"),
                            req.getParameter("password"), req.getParameter("role"), req.getParameter("active") != null);
                    redirectWithSuccess(req, resp, "User created.", "/admin/users");
                    return;
                case "update":
                    userService.update(admin, ValidationUtil.parseId(req.getParameter("id"), "user id"), req.getParameter("name"),
                            req.getParameter("email"), req.getParameter("phone"), req.getParameter("password"),
                            req.getParameter("role"), req.getParameter("active") != null);
                    redirectWithSuccess(req, resp, "User updated.", "/admin/users");
                    return;
                case "delete":
                    userService.delete(admin, ValidationUtil.parseId(req.getParameter("id"), "user id"));
                    redirectWithSuccess(req, resp, "User deleted.", "/admin/users");
                    return;
                case "toggle":
                    userService.toggleActive(admin, ValidationUtil.parseId(req.getParameter("id"), "user id"));
                    redirectWithSuccess(req, resp, "User status changed.", "/admin/users");
                    return;
                default:
                    throw new ValidationException("Unknown action.");
            }
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/admin/users");
        }
    }
}
