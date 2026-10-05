package com.petfeet.util;

import com.petfeet.exception.DatabaseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Creates/seeds the isolated PostgreSQL schema used by the hosted deployment. */
public final class DatabaseInitializer {
    private DatabaseInitializer() { }

    public static void initializeHostedDatabase() throws DatabaseException {
        if (!DBConnection.isPostgres()) return; // local MySQL was created with database.sql

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                // Serialize startup across overlapping deployments of this schema.
                try (PreparedStatement lock = c.prepareStatement("SELECT pg_advisory_xact_lock(hashtext(?))")) {
                    lock.setString(1, "petfeet:init:" + DBConnection.postgresSchema());
                    lock.execute();
                }
                try (Statement st = c.createStatement()) {
                    st.execute("CREATE TABLE IF NOT EXISTS petfeet_schema_version ("
                            + "version INTEGER PRIMARY KEY, installed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)");
                }
                boolean initialized;
                try (Statement st = c.createStatement();
                     ResultSet rs = st.executeQuery("SELECT 1 FROM petfeet_schema_version WHERE version = 1")) {
                    initialized = rs.next();
                }
                if (!initialized) {
                    for (String sql : readStatements("postgres.sql")) {
                        try (Statement st = c.createStatement()) {
                            st.execute(sql);
                        }
                    }
                    try (Statement st = c.createStatement()) {
                        st.execute("INSERT INTO petfeet_schema_version (version) VALUES (1)");
                    }
                }
                c.commit();
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new DatabaseException("Could not initialise the hosted PetFeet database", e);
        }
    }

    private static List<String> readStatements(String resource) throws IOException {
        InputStream in = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resource);
        if (in == null) throw new IOException("Missing database resource: " + resource);
        StringBuilder cleaned = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.startsWith("--")) cleaned.append(line).append('\n');
            }
        }
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;
        for (int i = 0; i < cleaned.length(); i++) {
            char ch = cleaned.charAt(i);
            if (ch == '\'' && (i + 1 >= cleaned.length() || cleaned.charAt(i + 1) != '\'')) inString = !inString;
            else if (ch == '\'' && inString && i + 1 < cleaned.length() && cleaned.charAt(i + 1) == '\'') {
                current.append(ch).append(cleaned.charAt(++i));
                continue;
            }
            if (ch == ';' && !inString) {
                String sql = current.toString().trim();
                if (!sql.isEmpty()) statements.add(sql);
                current.setLength(0);
            } else current.append(ch);
        }
        String tail = current.toString().trim();
        if (!tail.isEmpty()) statements.add(tail);
        return statements;
    }
}
