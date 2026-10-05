package com.petfeet.servlet;

import com.petfeet.exception.AuthenticationException;
import com.petfeet.exception.PetFeetException;
import com.petfeet.model.AdoptionApplication;
import com.petfeet.model.Pet;
import com.petfeet.model.User;
import com.petfeet.service.AdoptionService;
import com.petfeet.service.PetService;
import com.petfeet.util.ValidationUtil;
import com.petfeet.util.WebUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Adoption application form (GET), submission (POST) and the success page (GET ?success=ID). */
@WebServlet("/adopter/apply")
public class ApplyAdoptionServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AdoptionService adoptionService = new AdoptionService();
    private final PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        try {
            if (!param(req, "success").isEmpty()) {
                AdoptionApplication app = adoptionService.get(ValidationUtil.parseId(req.getParameter("success"), "application"));
                if (app.getAdopterId() != me.getId()) throw new AuthenticationException("This application does not belong to you.");
                req.setAttribute("application", app);
                view(req, resp, "adopter/apply-success");
                return;
            }
            Pet pet = petService.getVisiblePet(ValidationUtil.parseId(req.getParameter("petId"), "pet id"), me);
            req.setAttribute("pet", pet);
            if (req.getAttribute("form_applicantName") == null) {
                req.setAttribute("form_applicantName", me.getName());
                req.setAttribute("form_phone", me.getPhone());
                req.setAttribute("form_email", me.getEmail());
                req.setAttribute("form_address", me.getAddress() == null ? "" : me.getAddress());
            }
            view(req, resp, "adopter/apply");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        try {
            int petId = ValidationUtil.parseId(req.getParameter("petId"), "pet id");
            int id = adoptionService.apply(me, petId, req.getParameter("applicantName"), req.getParameter("phone"),
                    req.getParameter("email"), req.getParameter("address"), req.getParameter("reason"),
                    req.getParameter("experience"), req.getParameter("homeType"), req.getParameter("hasOtherPets") != null,
                    req.getParameter("message"));
            WebUtil.redirect(req, resp, "/adopter/apply?success=" + id);
        } catch (PetFeetException e) {
            // show the form again with what the user typed and a friendly error
            for (String f : new String[]{"applicantName", "phone", "email", "address", "reason", "experience", "homeType", "message"}) {
                req.setAttribute("form_" + f, param(req, f));
            }
            req.setAttribute("form_hasOtherPets", req.getParameter("hasOtherPets") != null);
            try {
                req.setAttribute("pet", petService.getVisiblePet(ValidationUtil.parseId(req.getParameter("petId"), "pet id"), me));
                WebUtil.flashError(req, friendlyMessage(e));
                view(req, resp, "adopter/apply");
            } catch (PetFeetException inner) {
                showError(req, resp, inner);
            }
        }
    }
}
