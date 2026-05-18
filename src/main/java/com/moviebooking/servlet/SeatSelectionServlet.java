package com.moviebooking.servlet;

import com.moviebooking.model.Showtime;
import com.moviebooking.service.ShowtimeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/seats")
public class SeatSelectionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showSeatSelection(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String showtimeId = request.getParameter("showtimeId");
        String[] selectedSeatValues = request.getParameterValues("selectedSeats");

        if (selectedSeatValues == null || selectedSeatValues.length == 0) {
            showSeatSelection(request, response, "Please select at least one seat.");
            return;
        }

        ArrayList<String> selectedSeats = new ArrayList<>(Arrays.asList(selectedSeatValues));
        ShowtimeService showtimeService = new ShowtimeService(getServletContext());
        if (!showtimeService.holdSeats(showtimeId, selectedSeats)) {
            showSeatSelection(request, response, "One or more selected seats are no longer available.");
            return;
        }

        request.getSession(true).setAttribute("selectedShowtimeId", showtimeId);
        request.getSession(true).setAttribute("selectedSeats", selectedSeats);
        response.sendRedirect(request.getContextPath() + "/checkout");
    }

    private void showSeatSelection(HttpServletRequest request, HttpServletResponse response, String error) throws ServletException, IOException {
        String showtimeId = request.getParameter("showtimeId");
        ShowtimeService showtimeService = new ShowtimeService(getServletContext());
        Showtime showtime = showtimeService.getShowtimeById(showtimeId);

        if (showtime == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Showtime not found.");
            return;
        }

        request.setAttribute("showtime", showtime);
        request.setAttribute("movieTitle", showtimeService.getMovieTitle(showtime.getMovieId()));
        request.setAttribute("seatMap", showtime.getSeatMap());
        request.setAttribute("seatPrice", 1500.00);
        if (error != null) {
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher("/WEB-INF/views/seat-selection.jsp").forward(request, response);
    }
}
