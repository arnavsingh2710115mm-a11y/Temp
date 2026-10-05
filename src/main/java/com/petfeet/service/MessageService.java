package com.petfeet.service;

import com.petfeet.exception.PetFeetException;
import com.petfeet.model.Message;
import com.petfeet.model.User;

import java.util.List;

/** Contract for the internal shelter - adopter messaging system. */
public interface MessageService {
    void send(User sender, int receiverId, String body) throws PetFeetException;
    List<Message> inbox(int userId) throws PetFeetException;
    List<Message> sent(int userId) throws PetFeetException;
    long unreadCount(int userId) throws PetFeetException;
}
