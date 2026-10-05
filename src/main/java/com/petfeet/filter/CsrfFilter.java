package com.petfeet.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * CSRF protection. Each session gets a random token (exposed to JSPs as ${csrfToken});
 * every POST must send it back as the "_csrf" parameter or the "X-CSRF-TOKEN" header.
 */
@WebFilter("/*")
public class CsrfFilter implements Filter {
    public static final String TOKEN_ATTRIBUTE = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(TOKEN_ATTRIBUTE);
        if (token == null) {
            byte[] bytes = new byte[24];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(TOKEN_ATTRIBUTE, token);
        }
        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String sent = req.getParameter("_csrf");
            if (sent == null) sent = req.getHeader("X-CSRF-TOKEN");
            if (!token.equals(sent)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Your session expired or the form was invalid. Please go back, refresh and try again.");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
