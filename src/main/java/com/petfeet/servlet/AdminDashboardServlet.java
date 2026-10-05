package com.petfeet.servlet;

import com.petfeet.exception.PetFeetException;
import com.petfeet.service.ActivityService;
import com.petfeet.service.AnalyticsService;
import com.petfeet.service.BackgroundServiceManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Admin overview: key numbers, charts, recent activity and the Background Services panel. */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final AnalyticsService analytics = new AnalyticsService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("stats", analytics.adminStats());
            BackgroundServiceManager bg = BackgroundServiceManager.getInstance();
            req.setAttribute("bgStatus", bg.getStatus());
            req.setAttribute("bgQueueSize", bg.getQueue().size());
            req.setAttribute("bgSnapshot", bg.getStatsCache().read());       // synchronized read of the shared cache
            req.setAttribute("bgSnapshotTime", bg.getStatsCache().getUpdatedAt());
            req.setAttribute("totalRequests", BaseServlet.getTotalRequests());
            req.setAttribute("recentLogs", new com.petfeet.dao.ActivityLogDAO().recent(6));
            view(req, resp, "admin/dashboard");
        } catch (PetFeetException e) {
            showError(req, resp, e);
        }
    }
}
