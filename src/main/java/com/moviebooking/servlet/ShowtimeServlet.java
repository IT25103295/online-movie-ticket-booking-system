package com.moviebooking.servlet;

import com.moviebooking.service.ShowtimeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/showtimes")
public class ShowtimeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String movieId = request.getParameter("movieId");
        ShowtimeService showtimeService = new ShowtimeService(getServletContext());

        request.setAttribute("movieId", movieId);
        request.setAttribute("movieTitle", showtimeService.getMovieTitle(movieId));
        request.setAttribute("showtimes", showtimeService.getShowtimesByMovieId(movieId));
        request.getRequestDispatcher("/WEB-INF/views/showtimes.jsp").forward(request, response);
    }
}
