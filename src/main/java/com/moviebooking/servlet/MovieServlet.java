package com.moviebooking.servlet;

import com.moviebooking.service.MovieService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@WebServlet("/movies")
public class MovieServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sort = request.getParameter("sort");
        MovieService movieService = new MovieService(getServletContext());

        request.setAttribute("movies", movieService.getSortedMovies(sort));
        request.setAttribute("selectedSort", sort == null || sort.trim().isEmpty() ? "title" : sort);
        request.getRequestDispatcher("/WEB-INF/views/movies.jsp").forward(request, response);
    }
}
