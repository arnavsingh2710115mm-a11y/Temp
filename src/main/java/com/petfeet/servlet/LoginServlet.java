package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.User;
import com.petfeet.service.AuthService;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Shows the login form and creates the HTTP session after a successful login. */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final String REMEMBER_COOKIE = "pf_email";
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User current = user(req);
        if (current != null) {
            WebUtil.redirect(req, resp, current.getDashboardPath());
            return;
        }
        String remembered = readRememberedEmail(req);
        if (!remembered.isEmpty()) {
            req.setAttribute("email", remembered);
            req.setAttribute("remember", true);
        }
        view(req, resp, "public/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email");
        try {
            User user = authService.login(email, req.getParameter("password"));

            req.changeSessionId();                       // new session id after login (prevents session fixation)
            HttpSession session = req.getSession();
            session.setAttribute(WebUtil.SESSION_USER_ID, user.getId());
            session.setAttribute(WebUtil.SESSION_ROLE, user.getRole());
            session.setAttribute(WebUtil.SESSION_USER, user);
            session.setMaxInactiveInterval(30 * 60);     // 30 minutes of inactivity

            writeRememberCookie(req, resp, user.getEmail(), req.getParameter("remember") != null);
            WebUtil.redirect(req, resp, user.getDashboardPath());   // ADMIN / SHELTER / ADOPTER dashboards
        } catch (PetFeetException e) {
            req.setAttribute("email", email);
            req.setAttribute("remember", req.getParameter("remember") != null);
            WebUtil.flashError(req, friendlyMessage(e));
            view(req, resp, "public/login");
        }
    }

    private static String readRememberedEmail(HttpServletRequest req) {
        if (req.getCookies() == null) return "";
        for (Cookie c : req.getCookies()) {
            if (REMEMBER_COOKIE.equals(c.getName())) return URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
        }
        return "";
    }

    private static void writeRememberCookie(HttpServletRequest req, HttpServletResponse resp, String email, boolean remember) {
        Cookie cookie = new Cookie(REMEMBER_COOKIE, remember ? URLEncoder.encode(email, StandardCharsets.UTF_8) : "");
        cookie.setHttpOnly(true);
        cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        cookie.setMaxAge(remember ? 30 * 24 * 3600 : 0);
        resp.addCookie(cookie);
    }
}
