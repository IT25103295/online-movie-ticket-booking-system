package com.moviebooking.servlet;

import com.moviebooking.model.Showtime;
import com.moviebooking.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/admin/showtimes", "/admin/showtimes/update", "/admin/showtimes/delete"})
public class AdminShowtimesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService adminService = new AdminService(getServletContext());
        request.setAttribute("showtimes", adminService.getShowtimes());
        request.setAttribute("movies", adminService.getMovies());
        request.getRequestDispatcher("/WEB-INF/views/admin-showtimes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService adminService = new AdminService(getServletContext());
        String path = request.getServletPath();
        if (path.endsWith("/delete")) {
            adminService.deleteShowtime(request.getParameter("id"));
        } else if (path.endsWith("/update")) {
            adminService.updateShowtime(readShowtime(request));
        } else {
            adminService.addShowtime(readShowtime(request));
        }
        response.sendRedirect(request.getContextPath() + "/admin/showtimes");
    }

    private Showtime readShowtime(HttpServletRequest request) {
        return new Showtime(
                request.getParameter("id"),
                request.getParameter("movieId"),
                request.getParameter("cinemaHall"),
                request.getParameter("date"),
                request.getParameter("time"));
    }
}
