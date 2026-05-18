<%@ page import="com.moviebooking.model.Booking" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    List<Booking> bookings = (List<Booking>) request.getAttribute("bookings");
    String customerEmail = (String) request.getAttribute("customerEmail");
%>

<main class="booking-history-page">
    <section class="container history-wrap">
        <span class="hero-kicker">Booking History</span>
        <h1>My Bookings</h1>
        <p class="muted-copy">Showing tickets for <strong><%= customerEmail %></strong></p>

        <% if (bookings == null || bookings.isEmpty()) { %>
            <div class="empty-panel">
                <h2>No bookings found</h2>
                <p>Your confirmed bookings will appear here.</p>
                <a class="btn btn-danger" href="${pageContext.request.contextPath}/movies">Movies</a>
            </div>
        <% } else { %>
            <div class="booking-list">
                <% for (Booking booking : bookings) { %>
                    <article class="booking-card">
                        <div>
                            <span class="booking-status"><%= booking.getStatus() %></span>
                            <h2><%= booking.getMovieTitle() %></h2>
                            <p><%= booking.getShowtimeDate() %> at <%= booking.getShowtimeTime() %> | <%= booking.getCinemaHall() %></p>
                        </div>
                        <div class="booking-meta">
                            <span><strong>ID</strong> <%= booking.getBookingId() %></span>
                            <span><strong>Seats</strong> <%= String.join(", ", booking.getSeats()) %></span>
                            <span><strong>Total</strong> LKR <%= String.format("%.2f", booking.getTotalPrice()) %></span>
                        </div>
                        <a class="btn btn-danger" href="${pageContext.request.contextPath}/ticket?bookingId=<%= booking.getBookingId() %>">View Ticket</a>
                    </article>
                <% } %>
            </div>
        <% } %>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
