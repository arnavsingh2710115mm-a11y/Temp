package com.petfeet.servlet;

import com.petfeet.service.PetService;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Search suggestions (JSON array of names, breeds and locations) used by the search box. */
@WebServlet("/suggest")
public class SuggestServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> values = new ArrayList<>();
        try {
            values = petService.suggestions(req.getParameter("q"));
        } catch (Exception e) {
            log("Suggestion lookup failed", e);
        }
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(WebUtil.jsonArray(values));
    }
}
