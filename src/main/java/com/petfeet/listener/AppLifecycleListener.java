package com.petfeet.listener;

import com.petfeet.exception.DatabaseException;
import com.petfeet.service.BackgroundServiceManager;
import com.petfeet.util.DatabaseInitializer;
import com.petfeet.util.DBConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/** Starts the background threads when the web application starts and stops them cleanly on shutdown. */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("maxPetImageMB", DBConnection.isPostgres() ? 2 : 5);
        try {
            DatabaseInitializer.initializeHostedDatabase();
        } catch (DatabaseException e) {
            throw new IllegalStateException("PetFeet could not initialise its hosted database", e);
        }
        BackgroundServiceManager.getInstance().start();
        sce.getServletContext().log("PetFeet started: database ready and background services running");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        BackgroundServiceManager.getInstance().stop();
        sce.getServletContext().log("PetFeet stopped: background services shut down");
    }
}
