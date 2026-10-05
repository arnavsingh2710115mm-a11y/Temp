package com.petfeet.servlet;

import com.petfeet.dao.NotificationDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.util.ValidationUtil;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Notification centre for every role. */
@WebServlet("/notifications")
public class NotificationServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("notifications", notificationDAO.findByUser(user(req).getId()));
            view(req, resp, "common/notifications");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = user(req).getId();
        try {
            switch (param(req, "action")) {
                case "readAll":
                    notificationDAO.markAllRead(userId);
                    break;
                case "read":
                    notificationDAO.markRead(ValidationUtil.parseId(req.getParameter("id"), "notification"), userId);
                    break;
                case "delete":
                    notificationDAO.deleteOwned(ValidationUtil.parseId(req.getParameter("id"), "notification"), userId);
                    break;
                default:
                    break;
            }
            WebUtil.redirect(req, resp, "/notifications");
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/notifications");
        }
    }
}
