package com.petfeet.service;

import com.petfeet.dao.MessageDAO;
import com.petfeet.dao.UserDAO;
import com.petfeet.exception.PetFeetException;
import com.petfeet.exception.ResourceNotFoundException;
import com.petfeet.exception.ValidationException;
import com.petfeet.model.Message;
import com.petfeet.model.Role;
import com.petfeet.model.User;
import com.petfeet.util.ValidationUtil;

import java.util.List;

/** Shelter <-> adopter messaging. The notification channel is injected, so it can be swapped (polymorphism). */
public class MessageServiceImpl implements MessageService {
    private final MessageDAO messageDAO = new MessageDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NotificationService notifier;

    public MessageServiceImpl(NotificationService notifier) { this.notifier = notifier; }

    public MessageServiceImpl() { this(new QueuedNotificationService()); }

    @Override
    public void send(User sender, int receiverId, String body) throws PetFeetException {
        String text = ValidationUtil.required(body, "Message", 1000);
        User receiver = userDAO.findById(receiverId);
        if (receiver == null) throw new ResourceNotFoundException("The recipient could not be found.");
        boolean validPair = (sender.getRole() == Role.SHELTER && receiver.getRole() == Role.ADOPTER)
                || (sender.getRole() == Role.ADOPTER && receiver.getRole() == Role.SHELTER);
        if (!validPair) throw new ValidationException("Messages can only be exchanged between a shelter and an adopter.");

        Message m = new Message();
        m.setSenderId(sender.getId());
        m.setReceiverId(receiverId);
        m.setBody(text);
        messageDAO.create(m);
        notifier.notifyUser(receiverId, "New message from " + sender.getName());
    }

    @Override public List<Message> inbox(int userId) throws PetFeetException { return messageDAO.inbox(userId); }
    @Override public List<Message> sent(int userId) throws PetFeetException { return messageDAO.sent(userId); }
    @Override public long unreadCount(int userId) throws PetFeetException { return messageDAO.unreadCount(userId); }

    public void markRead(int messageId, int userId) throws PetFeetException { messageDAO.markRead(messageId, userId); }
    public void delete(int messageId, int userId) throws PetFeetException { messageDAO.deleteOwned(messageId, userId); }
}
