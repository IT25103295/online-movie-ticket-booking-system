<%@ page import="com.moviebooking.model.Movie" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    ArrayList<Movie> movies = (ArrayList<Movie>) request.getAttribute("movies");
    String selectedSort = (String) request.getAttribute("selectedSort");
    if (selectedSort == null) {
        selectedSort = "title";
    }
%>

<main>
    <section class="movie-hero">
        <div class="container">
            <div class="movie-hero-content">
                <span class="hero-kicker">Now Showing</span>
                <h1>CineFlex Movie Lineup</h1>
                <p>Browse the latest cinema picks, compare ticket prices, and find the right show for your night out.</p>
                <a class="btn btn-danger" href="#movieGrid">Explore Movies</a>
            </div>
        </div>
    </section>

    <section class="container movie-section" id="movieGrid">
        <div class="section-toolbar">
            <div>
                <span class="hero-kicker">Movies</span>
                <h2>Available Shows</h2>
            </div>

            <form class="sort-form" method="get" action="${pageContext.request.contextPath}/movies">
                <label class="form-label" for="sort">Sort by</label>
                <select class="form-select" id="sort" name="sort" onchange="this.form.submit()">
                    <option value="title" <%= "title".equals(selectedSort) ? "selected" : "" %>>Title</option>
                    <option value="rating" <%= "rating".equals(selectedSort) ? "selected" : "" %>>Rating</option>
                    <option value="price" <%= "price".equals(selectedSort) ? "selected" : "" %>>Price</option>
                    <option value="duration" <%= "duration".equals(selectedSort) ? "selected" : "" %>>Duration</option>
                </select>
            </form>
        </div>

        <div class="row g-4">
            <% if (movies != null) {
                for (Movie movie : movies) { %>
                    <div class="col-12 col-sm-6 col-lg-3">
                        <article class="movie-card">
                            <img class="movie-poster" src="<%= movie.getPosterUrl() %>" alt="<%= movie.getTitle() %> poster">
                            <div class="movie-card-body">
                                <span class="movie-genre"><%= movie.getGenre() %> | <%= movie.getAgeRating() %></span>
                                <h3><%= movie.getTitle() %></h3>
                                <div class="movie-meta">
                                    <span>Rating <strong><%= movie.getRating() %></strong></span>
                                    <span><%= movie.getDurationMinutes() %> min</span>
                                </div>
                                <div class="movie-price">LKR <%= String.format("%.2f", movie.getPrice()) %></div>
                                <div class="movie-actions">
                                    <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/movie-details?id=<%= movie.getId() %>">View Details</a>
                                    <a class="btn btn-danger btn-sm" href="${pageContext.request.contextPath}/showtimes?movieId=<%= movie.getId() %>">Buy Tickets</a>
                                </div>
                            </div>
                        </article>
                    </div>
            <%  }
            } %>
        </div>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
