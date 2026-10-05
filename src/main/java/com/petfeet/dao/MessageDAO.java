package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database access for the messages table. */
public class MessageDAO extends BaseDAO implements CRUDOperations<Message, Integer> {

    private static final String SELECT =
            "SELECT m.id, m.sender_id, m.receiver_id, m.message, m.created_at, m.is_read, s.name AS sender_name, r.name AS receiver_name "
          + "FROM messages m JOIN users s ON s.id = m.sender_id JOIN users r ON r.id = m.receiver_id ";

    private static Message map(ResultSet rs) throws SQLException {
        Message m = new Message();
        m.setId(rs.getInt("id"));
        m.setSenderId(rs.getInt("sender_id"));
        m.setReceiverId(rs.getInt("receiver_id"));
        m.setBody(rs.getString("message"));
        m.setCreatedAt(rs.getTimestamp("created_at"));
        m.setRead(rs.getBoolean("is_read"));
        m.setSenderName(rs.getString("sender_name"));
        m.setReceiverName(rs.getString("receiver_name"));
        return m;
    }

    @Override
    public Integer create(Message m) throws DatabaseException {
        return insert("INSERT INTO messages (sender_id, receiver_id, message) VALUES (?,?,?)", m.getSenderId(), m.getReceiverId(), m.getBody());
    }

    @Override
    public Message findById(Integer id) throws DatabaseException {
        return queryOne(SELECT + "WHERE m.id = ?", MessageDAO::map, id);
    }

    @Override
    public List<Message> findAll() throws DatabaseException {
        return queryList(SELECT + "ORDER BY m.created_at DESC", MessageDAO::map);
    }

    @Override
    public boolean update(Message m) throws DatabaseException {
        return update("UPDATE messages SET message = ?, is_read = ? WHERE id = ?", m.getBody(), m.isRead(), m.getId()) > 0;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        return update("DELETE FROM messages WHERE id = ?", id) > 0;
    }

    public List<Message> inbox(int userId) throws DatabaseException {
        return queryList(SELECT + "WHERE m.receiver_id = ? ORDER BY m.created_at DESC", MessageDAO::map, userId);
    }

    public List<Message> sent(int userId) throws DatabaseException {
        return queryList(SELECT + "WHERE m.sender_id = ? ORDER BY m.created_at DESC", MessageDAO::map, userId);
    }

    public long unreadCount(int userId) throws DatabaseException {
        return count("SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = FALSE", userId);
    }

    public boolean markRead(int messageId, int receiverId) throws DatabaseException {
        return update("UPDATE messages SET is_read = TRUE WHERE id = ? AND receiver_id = ?", messageId, receiverId) > 0;
    }

    public boolean deleteOwned(int messageId, int userId) throws DatabaseException {
        return update("DELETE FROM messages WHERE id = ? AND (sender_id = ? OR receiver_id = ?)", messageId, userId, userId) > 0;
    }
}
