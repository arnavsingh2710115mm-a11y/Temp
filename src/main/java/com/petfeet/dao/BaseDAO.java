package com.petfeet.dao;

import com.petfeet.exception.DatabaseException;
import com.petfeet.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class with reusable JDBC plumbing. Every query uses PreparedStatement, ResultSet mapping through a
 * generic RowMapper, and try-with-resources so connections are always closed.
 * Methods that take a Connection take part in a caller-managed transaction.
 */
public abstract class BaseDAO {

    /** Converts one ResultSet row to an object of type T. */
    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    private static void bind(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    protected <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... params) throws DatabaseException {
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> rows = new ArrayList<>();
                while (rs.next()) rows.add(mapper.map(rs));
                return rows;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database query failed", e);
        }
    }

    /** Returns the first row or null when nothing matches. */
    protected <T> T queryOne(String sql, RowMapper<T> mapper, Object... params) throws DatabaseException {
        List<T> rows = queryList(sql, mapper, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    protected long count(String sql, Object... params) throws DatabaseException {
        Long value = queryOne(sql, rs -> rs.getLong(1), params);
        return value == null ? 0 : value;
    }

    /** Runs a "SELECT label, COUNT(*) ... GROUP BY label" query into an ordered map. */
    protected Map<String, Long> groupCount(String sql, Object... params) throws DatabaseException {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : queryList(sql, rs -> new Object[]{rs.getString(1), rs.getLong(2)}, params)) {
            result.put(String.valueOf(row[0]), (Long) row[1]);
        }
        return result;
    }

    protected int update(String sql, Object... params) throws DatabaseException {
        try (Connection c = DBConnection.getConnection()) {
            return update(c, sql, params);
        } catch (SQLException e) {
            throw new DatabaseException("Database update failed", e);
        }
    }

    protected int update(Connection c, String sql, Object... params) throws DatabaseException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Database update failed", e);
        }
    }

    protected int insert(String sql, Object... params) throws DatabaseException {
        try (Connection c = DBConnection.getConnection()) {
            return insert(c, sql, params);
        } catch (SQLException e) {
            throw new DatabaseException("Database insert failed", e);
        }
    }

    /** Executes an INSERT and returns the generated primary key. */
    protected int insert(Connection c, String sql, Object... params) throws DatabaseException {
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, params);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database insert failed", e);
        }
    }
}
