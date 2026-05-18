package com.moviebooking.servlet;

import com.moviebooking.model.Movie;
import com.moviebooking.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/admin/movies", "/admin/movies/update", "/admin/movies/delete"})
public class AdminMoviesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("movies", new AdminService(getServletContext()).getMovies());
        request.getRequestDispatcher("/WEB-INF/views/admin-movies.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AdminService adminService = new AdminService(getServletContext());
        String path = request.getServletPath();
        if (path.endsWith("/delete")) {
            adminService.deleteMovie(request.getParameter("id"));
        } else if (path.endsWith("/update")) {
            adminService.updateMovie(readMovie(request));
        } else {
            adminService.addMovie(readMovie(request));
        }
        response.sendRedirect(request.getContextPath() + "/admin/movies");
    }

    private Movie readMovie(HttpServletRequest request) {
        return new Movie(
                request.getParameter("id"),
                request.getParameter("title"),
                request.getParameter("description"),
                request.getParameter("genre"),
                parseDouble(request.getParameter("rating")),
                parseInt(request.getParameter("durationMinutes")),
                parseDouble(request.getParameter("price")),
                request.getParameter("posterUrl"),
                request.getParameter("bannerUrl"),
                request.getParameter("ageRating"));
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
