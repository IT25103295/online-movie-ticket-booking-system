<%@ page import="com.moviebooking.model.Movie" %>
<%@ page import="com.moviebooking.model.Showtime" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    List<Showtime> showtimes = (List<Showtime>) request.getAttribute("showtimes");
    List<Movie> movies = (List<Movie>) request.getAttribute("movies");
%>

<main class="admin-page">
    <section class="container admin-shell">
        <div class="admin-heading"><span class="hero-kicker">Admin Panel</span><h1>Showtimes</h1></div>
        <div class="admin-tabs">
            <a href="${pageContext.request.contextPath}/admin">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/movies">Movies</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/showtimes">Showtimes</a>
            <a href="${pageContext.request.contextPath}/admin/bookings">Bookings</a>
        </div>

        <section class="admin-panel">
            <h2>Add Showtime</h2>
            <form class="admin-form" method="post" action="${pageContext.request.contextPath}/admin/showtimes">
                <select name="movieId" class="form-select" required>
                    <% for (Movie movie : movies) { %>
                        <option value="<%= movie.getId() %>"><%= movie.getTitle() %> (<%= movie.getId() %>)</option>
                    <% } %>
                </select>
                <input name="cinemaHall" class="form-control" placeholder="Cinema Hall" required>
                <input name="date" class="form-control" placeholder="Date" required>
                <input name="time" class="form-control" placeholder="Time" required>
                <button class="btn btn-danger" type="submit">Add Showtime</button>
            </form>
        </section>

        <section class="admin-panel">
            <h2>Showtime Records</h2>
            <div class="table-responsive">
                <table class="table table-dark table-bordered align-middle">
                    <thead><tr><th>ID</th><th>Movie ID</th><th>Hall</th><th>Date</th><th>Time</th><th>Actions</th></tr></thead>
                    <tbody>
                    <% for (Showtime showtime : showtimes) { %>
                        <tr>
                            <form method="post" action="${pageContext.request.contextPath}/admin/showtimes/update">
                                <input type="hidden" name="id" value="<%= showtime.getId() %>">
                                <td><%= showtime.getId() %></td>
                                <td><input class="form-control form-control-sm" name="movieId" value="<%= showtime.getMovieId() %>"></td>
                                <td><input class="form-control form-control-sm" name="cinemaHall" value="<%= showtime.getCinemaHall() %>"></td>
                                <td><input class="form-control form-control-sm" name="date" value="<%= showtime.getDate() %>"></td>
                                <td><input class="form-control form-control-sm" name="time" value="<%= showtime.getTime() %>"></td>
                                <td class="table-actions"><button class="btn btn-outline-light btn-sm" type="submit">Edit</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/admin/showtimes/delete">
                                <input type="hidden" name="id" value="<%= showtime.getId() %>">
                                <button class="btn btn-danger btn-sm" type="submit">Delete</button></td>
                            </form>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
