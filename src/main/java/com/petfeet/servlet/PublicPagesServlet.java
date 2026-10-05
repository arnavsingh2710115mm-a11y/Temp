package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.User;
import com.petfeet.service.NotificationService;
import com.petfeet.service.PetService;
import com.petfeet.service.QueuedNotificationService;
import com.petfeet.service.SettingsService;
import com.petfeet.service.UserService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Static-ish public pages: Shelters, About Us, Contact and the college "About Project / Java Concepts" page. */
@WebServlet({"/shelters", "/about", "/contact", "/about-project"})
public class PublicPagesServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserService();
    private final PetService petService = new PetService();
    private final SettingsService settings = new SettingsService();
    private final NotificationService notifier = new QueuedNotificationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        try {
            switch (path) {
                case "/shelters":
                    req.setAttribute("shelters", userService.shelters());
                    req.setAttribute("petCounts", petService.availableCountByShelter());
                    view(req, resp, "public/shelters");
                    break;
                case "/contact":
                    req.setAttribute("contactEmail", settings.all().getOrDefault("contact_email", "hello@petfeet.com"));
                    view(req, resp, "public/contact");
                    break;
                case "/about-project":
                    view(req, resp, "public/about-project");
                    break;
                default:
                    view(req, resp, "public/about");
            }
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    /** Contact form: the message becomes a notification for every admin. */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = ValidationUtil.required(req.getParameter("name"), "Name", 100);
            String email = ValidationUtil.email(req.getParameter("email"));
            String message = ValidationUtil.required(req.getParameter("message"), "Message", 500);
            for (User admin : userService.admins()) {
                notifier.notifyUser(admin.getId(), "Contact form - " + name + " (" + email + "): " + message);
            }
            redirectWithSuccess(req, resp, "Thanks " + name + "! Your message has been sent to the PetFeet team.", "/contact");
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/contact");
        }
    }
}
