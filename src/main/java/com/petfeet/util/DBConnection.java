package com.petfeet.util;

import com.petfeet.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Central JDBC connection factory.
 *
 * Local development keeps using src/main/resources/db.properties (MySQL).
 * Hosted deployments use DATABASE_URL (PostgreSQL).  The hosted PetFeet tables
 * live in their own PostgreSQL schema so they stay isolated from other apps that
 * may share the same Render PostgreSQL instance.
 */
public final class DBConnection {
    private static final Properties CONFIG = new Properties();
    private static final String DATABASE_URL = System.getenv("DATABASE_URL");
    private static final String POSTGRES_SCHEMA = validatedSchema(
            System.getenv().getOrDefault("PETFEET_DB_SCHEMA", "petfeet"));
    private static volatile boolean postgresSchemaCreated;

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) CONFIG.load(in);
            // Loading both drivers lets the exact same WAR run locally (MySQL) and online (PostgreSQL).
            Class.forName("com.mysql.cj.jdbc.Driver");
            Class.forName("org.postgresql.Driver");
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Cannot initialise database settings: " + e.getMessage());
        }
    }

    private DBConnection() { }

    public static boolean isPostgres() {
        return DATABASE_URL != null && !DATABASE_URL.isBlank();
    }

    private static String localSetting(String key) {
        String envKey = null;
        if ("db.url".equals(key)) envKey = "DB_URL";
        else if ("db.user".equals(key)) envKey = "DB_USER";
        else if ("db.password".equals(key)) envKey = "DB_PASSWORD";
        if (envKey != null) {
            String env = System.getenv(envKey);
            if (env != null && !env.isBlank()) return env;
        }
        return System.getProperty(key, CONFIG.getProperty(key));
    }

    public static Connection getConnection() throws DatabaseException {
        try {
            if (isPostgres()) return postgresConnection();
            return DriverManager.getConnection(localSetting("db.url"), localSetting("db.user"), localSetting("db.password"));
        } catch (Exception e) {
            throw new DatabaseException("Could not connect to the database.", e);
        }
    }

    private static Connection postgresConnection() throws Exception {
        URI uri = URI.create(DATABASE_URL);
        if (!"postgres".equalsIgnoreCase(uri.getScheme()) && !"postgresql".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("DATABASE_URL must be a PostgreSQL URL");
        }
        String[] credentials = uri.getRawUserInfo().split(":", 2);
        String host = uri.getHost();
        if (host != null && host.contains(":")) host = "[" + host + "]";
        int port = uri.getPort() < 0 ? 5432 : uri.getPort();
        String jdbc = "jdbc:postgresql://" + host + ":" + port + uri.getRawPath();
        if (uri.getRawQuery() != null && !uri.getRawQuery().isBlank()) jdbc += "?" + uri.getRawQuery();
        Connection c = DriverManager.getConnection(jdbc, decode(credentials[0]), credentials.length > 1 ? decode(credentials[1]) : "");
        try {
            ensurePostgresSchema(c);
            try (Statement st = c.createStatement()) {
                st.execute("SET search_path TO \"" + POSTGRES_SCHEMA + "\"");
            }
            return c;
        } catch (Exception e) {
            c.close();
            throw e;
        }
    }

    private static void ensurePostgresSchema(Connection c) throws SQLException {
        if (!postgresSchemaCreated) {
            synchronized (DBConnection.class) {
                if (!postgresSchemaCreated) {
                    try (Statement st = c.createStatement()) {
                        st.execute("CREATE SCHEMA IF NOT EXISTS \"" + POSTGRES_SCHEMA + "\"");
                    }
                    postgresSchemaCreated = true;
                }
            }
        }
    }

    public static String postgresSchema() {
        return POSTGRES_SCHEMA;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value.replace("+", "%2B"), StandardCharsets.UTF_8);
    }

    private static String validatedSchema(String value) {
        if (value == null || !value.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("PETFEET_DB_SCHEMA contains invalid characters");
        }
        return value;
    }
}
