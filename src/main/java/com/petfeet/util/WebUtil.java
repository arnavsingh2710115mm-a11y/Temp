package com.petfeet.util;

import com.petfeet.model.Role;
import com.petfeet.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Helpers shared by servlets: current user, flash messages, redirects and JSON escaping. */
public final class WebUtil {
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_ROLE = "userRole";
    public static final String SESSION_USER = "currentUser";
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";

    private WebUtil() { }

    public static User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(SESSION_USER);
    }

    public static Role currentRole(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (Role) session.getAttribute(SESSION_ROLE);
    }

    public static void flashSuccess(HttpServletRequest req, String message) {
        req.getSession(true).setAttribute(FLASH_SUCCESS, message);
    }

    public static void flashError(HttpServletRequest req, String message) {
        req.getSession(true).setAttribute(FLASH_ERROR, message);
    }

    /** Moves flash messages from the session to request attributes (shown once, then gone). */
    public static void exposeFlash(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return;
        for (String key : new String[]{FLASH_SUCCESS, FLASH_ERROR}) {
            Object value = session.getAttribute(key);
            if (value != null) {
                req.setAttribute(key, value);
                session.removeAttribute(key);
            }
        }
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    public static void forward(HttpServletRequest req, HttpServletResponse resp, String view) throws javax.servlet.ServletException, IOException {
        exposeFlash(req);
        req.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(req, resp);
    }

    public static String jsonString(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : (s == null ? "" : s).toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': break;
                case '\t': sb.append(' '); break;
                default:   sb.append(c < 0x20 ? ' ' : c);
            }
        }
        return sb.append('"').toString();
    }

    public static String jsonArray(List<String> values) {
        List<String> quoted = new ArrayList<>();
        for (String v : values) quoted.add(jsonString(v));
        return "[" + String.join(",", quoted) + "]";
    }
}
