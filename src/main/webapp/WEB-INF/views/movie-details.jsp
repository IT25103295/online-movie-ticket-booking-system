<%@ page import="com.moviebooking.model.Movie" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    Movie movie = (Movie) request.getAttribute("movie");
%>

<main>
    <section class="details-hero" style="background-image: linear-gradient(90deg, rgba(0,0,0,0.9), rgba(0,0,0,0.55)), url('<%= movie.getBannerUrl() %>');">
        <div class="container">
            <div class="details-layout">
                <img class="details-poster" src="<%= movie.getPosterUrl() %>" alt="<%= movie.getTitle() %> poster">
                <div class="details-copy">
                    <span class="hero-kicker"><%= movie.getGenre() %> | <%= movie.getAgeRating() %></span>
                    <h1><%= movie.getTitle() %></h1>
                    <p><%= movie.getDescription() %></p>
                    <div class="details-stats">
                        <span>Rating <strong><%= movie.getRating() %></strong></span>
                        <span><%= movie.getDurationMinutes() %> min</span>
                        <span>LKR <%= String.format("%.2f", movie.getPrice()) %></span>
                    </div>
                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/showtimes?movieId=<%= movie.getId() %>">Buy Tickets</a>
                </div>
            </div>
        </div>
    </section>

    <section class="container showtime-placeholder">
        <h2>Showtimes</h2>
        <p>Showtime selection will be available in the booking component.</p>
        <a class="btn btn-outline-light" href="${pageContext.request.contextPath}/movies">Back to Movies</a>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
