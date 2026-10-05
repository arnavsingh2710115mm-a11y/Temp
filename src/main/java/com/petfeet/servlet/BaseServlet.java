package com.petfeet.servlet;

import com.petfeet.dao.FavoriteDAO;
import com.petfeet.dao.MessageDAO;
import com.petfeet.dao.NotificationDAO;
import com.petfeet.exception.AuthenticationException;
import com.petfeet.exception.DatabaseException;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ResourceNotFoundException;
import com.petfeet.model.Role;
import com.petfeet.model.User;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Parent of all PetFeet servlets.
 *
 * SERVLET LIFECYCLE demonstrated here:
 *   init()     - called once by the container when the servlet is first loaded
 *   service()  - called for every request; dispatches to doGet() / doPost()
 *   destroy()  - called once when the web application is stopped
 */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final AtomicLong TOTAL_REQUESTS = new AtomicLong();

    public static long getTotalRequests() { return TOTAL_REQUESTS.get(); }

    @Override
    public void init() throws ServletException {
        getServletContext().log("[lifecycle] init() -> " + getClass().getSimpleName());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TOTAL_REQUESTS.incrementAndGet();
        super.service(req, resp);      // dispatches to doGet / doPost
    }

    @Override
    public void destroy() {
        getServletContext().log("[lifecycle] destroy() -> " + getClass().getSimpleName());
    }

    // ------------------------------------------------------------------ helpers
    protected User user(HttpServletRequest req) { return WebUtil.currentUser(req); }

    /** Forwards to a JSP under /WEB-INF/views and adds the header counters (bell + inbox). */
    protected void view(HttpServletRequest req, HttpServletResponse resp, String viewName) throws ServletException, IOException {
        User u = user(req);
        if (u != null) {
            try {
                req.setAttribute("unreadNotifications", new NotificationDAO().unreadCount(u.getId()));
                req.setAttribute("unreadMessages", new MessageDAO().unreadCount(u.getId()));
            } catch (DatabaseException e) {
                log("Could not load unread counters", e);
            }
        }
        WebUtil.forward(req, resp, viewName);
    }

    /** Loads the ids of the adopter's favourite pets so cards can show a filled heart. */
    protected void loadFavorites(HttpServletRequest req) {
        User u = user(req);
        if (u != null && u.getRole() == Role.ADOPTER) {
            try {
                req.setAttribute("favoriteIds", new FavoriteDAO().petIdsOf(u.getId()));
            } catch (DatabaseException e) {
                log("Could not load favourites", e);
            }
        }
    }

    /** Friendly text for any exception: PetFeet messages are shown, technical ones are hidden and logged. */
    protected String friendlyMessage(Exception e) {
        if (e instanceof DatabaseException) {
            log("Database error", e);
            return "Something went wrong while saving your data. Please try again in a moment.";
        }
        if (e instanceof PetFeetException) return e.getMessage();
        log("Unexpected error", e);
        return "Something unexpected happened. Please try again.";
    }

    /** Shows the "Oops! This paw went missing." page with a friendly message. */
    protected void showError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws ServletException, IOException {
        int status = e instanceof ResourceNotFoundException ? HttpServletResponse.SC_NOT_FOUND
                : e instanceof AuthenticationException ? HttpServletResponse.SC_FORBIDDEN
                : (e instanceof PetFeetException && !(e instanceof DatabaseException)) ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        req.setAttribute("errorStatus", status);
        req.setAttribute("errorMessage", friendlyMessage(e));
        resp.setStatus(status);
        view(req, resp, "common/error");
    }

    /** Flash an error and send the browser to another page (post/redirect/get). */
    protected void redirectWithError(HttpServletRequest req, HttpServletResponse resp, Exception e, String path) throws IOException {
        WebUtil.flashError(req, friendlyMessage(e));
        WebUtil.redirect(req, resp, path);
    }

    protected void redirectWithSuccess(HttpServletRequest req, HttpServletResponse resp, String message, String path) throws IOException {
        WebUtil.flashSuccess(req, message);
        WebUtil.redirect(req, resp, path);
    }

    protected static String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
