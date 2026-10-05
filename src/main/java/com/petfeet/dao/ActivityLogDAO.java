package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.model.ActivityLog;

import java.util.List;

/** Writes and reads the admin activity log. */
public class ActivityLogDAO extends BaseDAO {

    public void log(Integer userId, String action) throws DatabaseException {
        insert("INSERT INTO activity_logs (user_id, action) VALUES (?, ?)", userId, action);
    }

    public List<ActivityLog> recent(int limit) throws DatabaseException {
        return queryList("SELECT l.id, l.action, l.created_at, COALESCE(u.name, 'System') AS user_name FROM activity_logs l "
                + "LEFT JOIN users u ON u.id = l.user_id ORDER BY l.created_at DESC, l.id DESC LIMIT ?", rs -> {
            ActivityLog log = new ActivityLog();
            log.setId(rs.getInt("id"));
            log.setAction(rs.getString("action"));
            log.setUserName(rs.getString("user_name"));
            log.setCreatedAt(rs.getTimestamp("created_at"));
            return log;
        }, limit);
    }
}
