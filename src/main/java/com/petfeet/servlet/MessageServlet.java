package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Role;
import com.petfeet.model.User;
import com.petfeet.service.MessageService;
import com.petfeet.service.MessageServiceImpl;
import com.petfeet.service.UserService;
import com.petfeet.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/** Inbox, sent messages and compose form for shelters and adopters. */
@WebServlet("/messages")
public class MessageServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final MessageServiceImpl messageService = new MessageServiceImpl();
    private final MessageService messageContract = messageService;      // programmed against the interface
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        try {
            String tab = "sent".equals(param(req, "tab")) ? "sent" : "inbox";
            req.setAttribute("tab", tab);
            req.setAttribute("messages", "sent".equals(tab) ? messageContract.sent(me.getId()) : messageContract.inbox(me.getId()));
            List<User> contacts = me.getRole() == Role.SHELTER ? userService.adoptersForShelter(me.getId()) : userService.shelters();
            req.setAttribute("contacts", contacts);
            String to = param(req, "to");
            req.setAttribute("selectedReceiver", to.isEmpty() ? 0 : ValidationUtil.parseId(to, "recipient"));
            req.setAttribute("prefill", param(req, "text"));
            view(req, resp, "common/messages");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = user(req);
        String action = param(req, "action");
        try {
            switch (action) {
                case "send":
                    messageContract.send(me, ValidationUtil.parseId(req.getParameter("receiverId"), "recipient"), req.getParameter("body"));
                    redirectWithSuccess(req, resp, "Message sent.", "/messages?tab=sent");
                    return;
                case "read":
                    messageService.markRead(ValidationUtil.parseId(req.getParameter("id"), "message"), me.getId());
                    break;
                case "delete":
                    messageService.delete(ValidationUtil.parseId(req.getParameter("id"), "message"), me.getId());
                    redirectWithSuccess(req, resp, "Message deleted.", "/messages?tab=" + ("sent".equals(param(req, "tab")) ? "sent" : "inbox"));
                    return;
                default:
                    break;
            }
            com.petfeet.util.WebUtil.redirect(req, resp, "/messages");
        } catch (PetFeetException e) {
            redirectWithError(req, resp, e, "/messages");
        }
    }
}
