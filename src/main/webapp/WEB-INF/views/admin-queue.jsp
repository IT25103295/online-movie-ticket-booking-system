<%@ page import="com.moviebooking.model.BookingRequest" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    Integer queueSize = (Integer) request.getAttribute("queueSize");
    List<BookingRequest> pendingRequests = (List<BookingRequest>) request.getAttribute("pendingRequests");
    List<BookingRequest> processedRequests = (List<BookingRequest>) request.getAttribute("processedRequests");
    BookingRequest lastProcessed = (BookingRequest) request.getAttribute("lastProcessed");
%>

<main class="booking-page">
    <section class="container admin-queue-wrap">
        <span class="hero-kicker">Admin</span>
        <h1>Booking Queue Monitor</h1>

        <div class="queue-stats">
            <div class="summary-item"><span>Queue Length</span><strong><%= queueSize == null ? 0 : queueSize %></strong></div>
            <div class="summary-item"><span>Last Processed</span><strong><%= lastProcessed == null ? "None" : lastProcessed.getRequestId() %></strong></div>
        </div>

        <section class="queue-panel">
            <h2>Pending Requests</h2>
            <div class="table-responsive">
                <table class="table table-dark table-bordered align-middle">
                    <thead>
                    <tr><th>ID</th><th>Customer</th><th>Showtime</th><th>Seats</th><th>Total</th><th>Status</th></tr>
                    </thead>
                    <tbody>
                    <% if (pendingRequests != null && !pendingRequests.isEmpty()) {
                        for (BookingRequest requestItem : pendingRequests) { %>
                            <tr>
                                <td><%= requestItem.getRequestId() %></td>
                                <td><%= requestItem.getCustomerName() %></td>
                                <td><%= requestItem.getShowtimeId() %></td>
                                <td><%= String.join(", ", requestItem.getSelectedSeats()) %></td>
                                <td>LKR <%= String.format("%.2f", requestItem.getTotalPrice()) %></td>
                                <td><%= requestItem.getStatus() %></td>
                            </tr>
                    <%  }
                    } else { %>
                        <tr><td colspan="6">No pending requests.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="queue-panel">
            <h2>Processed Requests</h2>
            <div class="table-responsive">
                <table class="table table-dark table-bordered align-middle">
                    <thead>
                    <tr><th>ID</th><th>Customer</th><th>Showtime</th><th>Seats</th><th>Status</th><th>Reason</th></tr>
                    </thead>
                    <tbody>
                    <% if (processedRequests != null && !processedRequests.isEmpty()) {
                        for (BookingRequest requestItem : processedRequests) { %>
                            <tr>
                                <td><%= requestItem.getRequestId() %></td>
                                <td><%= requestItem.getCustomerName() %></td>
                                <td><%= requestItem.getShowtimeId() %></td>
                                <td><%= String.join(", ", requestItem.getSelectedSeats()) %></td>
                                <td><%= requestItem.getStatus() %></td>
                                <td><%= requestItem.getRejectionReason() %></td>
                            </tr>
                    <%  }
                    } else { %>
                        <tr><td colspan="6">No processed requests.</td></tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </section>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
