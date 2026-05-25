package com.moviebooking.servlet;

import com.moviebooking.model.Movie;
import com.moviebooking.service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/movie-details")
public class MovieDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        Movie movie = new MovieService(getServletContext()).getMovieById(id);

        if (movie == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Movie not found.");
            return;
        }

        request.setAttribute("movie", movie);
        request.getRequestDispatcher("/WEB-INF/views/movie-details.jsp").forward(request, response);
    }
}
