package com.petfeet.filter;

import com.petfeet.model.Role;
import com.petfeet.util.WebUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Session / role guard. Every URL under /admin, /shelter, /adopter and the shared logged-in pages must have a valid
 * session; /admin/* needs ADMIN, /shelter/* needs SHELTER, /adopter/* needs ADOPTER.
 */
@WebFilter(urlPatterns = {"/admin/*", "/shelter/*", "/adopter/*", "/messages", "/notifications", "/profile"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");   // back button must not show private pages after logout

        Role role = WebUtil.currentRole(req);
        if (role == null) {
            WebUtil.flashError(req, "Please log in to continue.");
            WebUtil.redirect(req, resp, "/login");
            return;
        }
        String path = req.getServletPath();
        Role required = path.startsWith("/admin") ? Role.ADMIN
                : path.startsWith("/shelter") ? Role.SHELTER
                : path.startsWith("/adopter") ? Role.ADOPTER : null;
        if (required != null && required != role) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have access to this area.");
            return;
        }
        if ("/messages".equals(path) && role == Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Messaging is for shelters and adopters.");
            return;
        }
        chain.doFilter(request, response);
    }
}
