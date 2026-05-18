<%@ page import="com.moviebooking.model.AdminDashboardSummary" %>
<%@ page import="com.moviebooking.model.AdminBookingRecord" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    AdminDashboardSummary summary = (AdminDashboardSummary) request.getAttribute("summary");
    List<AdminBookingRecord> recentBookings = (List<AdminBookingRecord>) request.getAttribute("recentBookings");
%>

<main class="admin-page">
    <section class="container admin-shell">
        <div class="admin-heading">
            <span class="hero-kicker">Admin Panel</span>
            <h1>Dashboard</h1>
        </div>
        <div class="admin-tabs">
            <a class="active" href="${pageContext.request.contextPath}/admin">Dashboard</a>
            <a href="${pageContext.request.contextPath}/admin/movies">Movies</a>
            <a href="${pageContext.request.contextPath}/admin/showtimes">Showtimes</a>
            <a href="${pageContext.request.contextPath}/admin/bookings">Bookings</a>
        </div>
        <div class="stat-grid">
            <div class="stat-card"><span>Total Movies</span><strong><%= summary.getTotalMovies() %></strong></div>
            <div class="stat-card"><span>Total Showtimes</span><strong><%= summary.getTotalShowtimes() %></strong></div>
            <div class="stat-card"><span>Queue Length</span><strong><%= summary.getQueueLength() %></strong></div>
            <div class="stat-card"><span>Recent Bookings</span><strong><%= summary.getRecentBookingsCount() %></strong></div>
        </div>
        <section class="admin-panel">
            <h2>Recent Bookings</h2>
            <div class="table-responsive">
                <table class="table table-dark table-bordered align-middle">
                    <thead><tr><th>ID</th><th>Customer</th><th>Movie</th><th>Seats</th><th>Status</th><th>Total</th></tr></thead>
                    <tbody>
                    <% if (recentBookings != null && !recentBookings.isEmpty()) {
                        for (AdminBookingRecord booking : recentBookings) { %>
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
