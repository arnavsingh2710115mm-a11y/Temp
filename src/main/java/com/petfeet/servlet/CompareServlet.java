package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.Pet;
import com.petfeet.service.PetService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Side-by-side comparison of two pets (?a=1&b=2). */
@WebServlet("/compare")
public class CompareServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int a = ValidationUtil.parseId(req.getParameter("a"), "first pet");
            int b = ValidationUtil.parseId(req.getParameter("b"), "second pet");
            if (a == b) throw new ValidationException("Please choose two different pets to compare.");
            List<Pet> pair = petService.compare(a, b);
            for (Pet p : pair) {
                if (!p.isAvailable() && !Pet.ADOPTED.equals(p.getStatus())) throw new ValidationException("Only listed pets can be compared.");
            }
            req.setAttribute("first", pair.get(0));
            req.setAttribute("second", pair.get(1));
            view(req, resp, "public/compare");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
