<%@ page import="com.moviebooking.model.Showtime" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    ArrayList<Showtime> showtimes = (ArrayList<Showtime>) request.getAttribute("showtimes");
    String movieId = (String) request.getAttribute("movieId");
    String movieTitle = (String) request.getAttribute("movieTitle");
%>

<main class="booking-page">
    <section class="booking-hero">
        <div class="container">
            <span class="hero-kicker">Showtimes</span>
            <h1><%= movieTitle %></h1>
            <p>Select a cinema hall and time, then pick your seats.</p>
        </div>
    </section>

    <section class="container showtime-section">
        <div class="section-heading">
            <span class="hero-kicker">Movie ID: <%= movieId == null ? "N/A" : movieId %></span>
            <h2>Available Showtimes</h2>
        </div>

        <div class="row g-4">
            <% if (showtimes != null && !showtimes.isEmpty()) {
                for (Showtime showtime : showtimes) { %>
                    <div class="col-12 col-md-6 col-lg-4">
                        <article class="showtime-card">
                            <div>
                                <span class="showtime-hall"><%= showtime.getCinemaHall() %></span>
                                <h3><%= showtime.getDate() %></h3>
                                <p><%= showtime.getTime() %></p>
                            </div>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/seats?showtimeId=<%= showtime.getId() %>">Pick Seats</a>
                        </article>
                    </div>
            <%  }
            } else { %>
                <div class="col-12">
                    <div class="empty-panel">No showtimes are available for this movie yet.</div>
                </div>
            <% } %>
        </div>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
