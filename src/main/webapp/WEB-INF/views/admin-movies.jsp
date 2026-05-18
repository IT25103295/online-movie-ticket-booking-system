<%@ page import="com.moviebooking.model.Movie" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    List<Movie> movies = (List<Movie>) request.getAttribute("movies");
%>

<main class="admin-page">
    <section class="container admin-shell">
        <div class="admin-heading"><span class="hero-kicker">Admin Panel</span><h1>Movies</h1></div>
        <div class="admin-tabs">
            <a href="${pageContext.request.contextPath}/admin">Dashboard</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/movies">Movies</a>
            <a href="${pageContext.request.contextPath}/admin/showtimes">Showtimes</a>
            <a href="${pageContext.request.contextPath}/admin/bookings">Bookings</a>
        </div>

        <section class="admin-panel">
            <h2>Add Movie</h2>
            <form class="admin-form" method="post" action="${pageContext.request.contextPath}/admin/movies">
                <input name="title" class="form-control" placeholder="Title" required>
                <input name="genre" class="form-control" placeholder="Genre" required>
                <input name="rating" class="form-control" placeholder="Rating" required>
                <input name="durationMinutes" class="form-control" placeholder="Duration" required>
                <input name="price" class="form-control" placeholder="Price" required>
                <input name="ageRating" class="form-control" placeholder="Age Rating">
                <input name="posterUrl" class="form-control" placeholder="Poster URL">
                <input name="bannerUrl" class="form-control" placeholder="Banner URL">
                <textarea name="description" class="form-control" placeholder="Description" required></textarea>
                <button class="btn btn-danger" type="submit">Add Movie</button>
            </form>
        </section>

        <section class="admin-panel">
            <h2>Movie Records</h2>
            <div class="table-responsive">
                <table class="table table-dark table-bordered align-middle">
                    <thead><tr><th>Title</th><th>Genre</th><th>Rating</th><th>Duration</th><th>Price</th><th>Actions</th></tr></thead>
                    <tbody>
                    <% for (Movie movie : movies) { %>
                        <tr>
                            <form method="post" action="${pageContext.request.contextPath}/admin/movies/update">
                                <input type="hidden" name="id" value="<%= movie.getId() %>">
                                <input type="hidden" name="description" value="<%= movie.getDescription() %>">
                                <input type="hidden" name="posterUrl" value="<%= movie.getPosterUrl() %>">
                                <input type="hidden" name="bannerUrl" value="<%= movie.getBannerUrl() %>">
                                <input type="hidden" name="ageRating" value="<%= movie.getAgeRating() %>">
                                <td><input class="form-control form-control-sm" name="title" value="<%= movie.getTitle() %>"></td>
                                <td><input class="form-control form-control-sm" name="genre" value="<%= movie.getGenre() %>"></td>
                                <td><input class="form-control form-control-sm" name="rating" value="<%= movie.getRating() %>"></td>
                                <td><input class="form-control form-control-sm" name="durationMinutes" value="<%= movie.getDurationMinutes() %>"></td>
                                <td><input class="form-control form-control-sm" name="price" value="<%= movie.getPrice() %>"></td>
                                <td class="table-actions"><button class="btn btn-outline-light btn-sm" type="submit">Edit</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/admin/movies/delete">
                                <input type="hidden" name="id" value="<%= movie.getId() %>">
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
