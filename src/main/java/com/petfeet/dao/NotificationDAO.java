package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.Notification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/** Database access for the notifications table. */
public class NotificationDAO extends BaseDAO implements CRUDOperations<Notification, Integer> {

    private static Notification map(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }

    @Override
    public Integer create(Notification n) throws DatabaseException {
        return insert("INSERT INTO notifications (user_id, message) VALUES (?,?)", n.getUserId(), n.getMessage());
    }

    /** Variant used inside a JDBC transaction. */
    public Integer create(Connection c, Notification n) throws DatabaseException {
        return insert(c, "INSERT INTO notifications (user_id, message) VALUES (?,?)", n.getUserId(), n.getMessage());
    }

    @Override
    public Notification findById(Integer id) throws DatabaseException {
        return queryOne("SELECT * FROM notifications WHERE id = ?", NotificationDAO::map, id);
    }

    @Override
    public List<Notification> findAll() throws DatabaseException {
        return queryList("SELECT * FROM notifications ORDER BY created_at DESC", NotificationDAO::map);
    }

    @Override
    public boolean update(Notification n) throws DatabaseException {
        return update("UPDATE notifications SET message = ?, is_read = ? WHERE id = ?", n.getMessage(), n.isRead(), n.getId()) > 0;
    }

    @Override
    public boolean delete(Integer id) throws DatabaseException {
        return update("DELETE FROM notifications WHERE id = ?", id) > 0;
    }

    public List<Notification> findByUser(int userId) throws DatabaseException {
        return queryList("SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC, id DESC LIMIT 100", NotificationDAO::map, userId);
    }

    public long unreadCount(int userId) throws DatabaseException {
        return count("SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE", userId);
    }

    public boolean markRead(int id, int userId) throws DatabaseException {
        return update("UPDATE notifications SET is_read = TRUE WHERE id = ? AND user_id = ?", id, userId) > 0;
    }

    public int markAllRead(int userId) throws DatabaseException {
        return update("UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE", userId);
    }

    public boolean deleteOwned(int id, int userId) throws DatabaseException {
        return update("DELETE FROM notifications WHERE id = ? AND user_id = ?", id, userId) > 0;
    }
}
