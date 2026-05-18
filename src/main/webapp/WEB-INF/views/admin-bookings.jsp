<%@ page import="com.moviebooking.model.AdminBookingRecord" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    List<AdminBookingRecord> bookings = (List<AdminBookingRecord>) request.getAttribute("bookings");
    Boolean hasQueueData = (Boolean) request.getAttribute("hasQueueData");
%>

<main class="admin-page">
    <section class="container admin-shell">
        <div class="admin-heading"><span class="hero-kicker">Admin Panel</span><h1>Bookings</h1></div>
        <div class="admin-tabs">
            <a href="${pageContext.request.contextPath}/admin">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/movies">Movies</a>
            <a href="${pageContext.request.contextPath}/admin/showtimes">Showtimes</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/bookings">Bookings</a>
        </div>
        <% if (Boolean.TRUE.equals(hasQueueData)) { %>
            <a class="btn btn-danger mb-3" href="${pageContext.request.contextPath}/admin/queue">Open Queue Monitor</a>
        <% } %>
        <section class="admin-panel">
            <h2>Recent Bookings</h2>
            <div class="table-responsive">
                <table class="table table-dark table-bordered align-middle">
                    <thead><tr><th>Booking ID</th><th>Customer</th><th>Movie</th><th>Seats</th><th>Status</th><th>Total</th></tr></thead>
                    <tbody>
                    <% if (bookings != null && !bookings.isEmpty()) {
                        for (AdminBookingRecord booking : bookings) { %>
                            <tr><td><%= booking.getBookingId() %></td><td><%= booking.getCustomer() %></td><td><%= booking.getMovie() %></td><td><%= booking.getSeats() %></td><td><%= booking.getStatus() %></td><td>LKR <%= String.format("%.2f", booking.getTotal()) %></td></tr>
                    <%  }
                    } else { %>
                        <tr><td colspan="6">No booking records yet.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
