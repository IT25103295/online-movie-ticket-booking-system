<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    ArrayList<String> selectedSeats = (ArrayList<String>) request.getAttribute("selectedSeats");
    String movieLabel = (String) request.getAttribute("movieLabel");
    String showtimeId = (String) request.getAttribute("showtimeId");
    Double seatPrice = (Double) request.getAttribute("seatPrice");
    Double totalPrice = (Double) request.getAttribute("totalPrice");
    Boolean hasSelection = (Boolean) request.getAttribute("hasSelection");
%>

<main class="booking-page">
    <section class="container checkout-wrap">
        <div class="checkout-card">
            <span class="hero-kicker">Checkout</span>
            <h1>Confirm Booking</h1>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
            <% } %>

            <% if (hasSelection == null || !hasSelection) { %>
                <div class="empty-panel">
                    <h2>No seats selected</h2>
                    <p>Please choose seats before confirming a booking.</p>
                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/movies">Movies</a>
                </div>
            <% } else { %>
                <div class="summary-grid">
                    <div class="summary-item"><span>Movie</span><strong><%= movieLabel %></strong></div>
                    <div class="summary-item"><span>Showtime</span><strong><%= showtimeId %></strong></div>
                    <div class="summary-item"><span>Seats</span><strong><%= String.join(", ", selectedSeats) %></strong></div>
                    <div class="summary-item"><span>Seat Price</span><strong>LKR <%= String.format("%.2f", seatPrice) %></strong></div>
                    <div class="summary-item total"><span>Total</span><strong>LKR <%= String.format("%.2f", totalPrice) %></strong></div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/checkout" class="checkout-form">
                    <div class="row g-3">
                        <div class="col-12 col-md-6">
                            <label class="form-label" for="customerName">Customer Name</label>
                            <input class="form-control" id="customerName" name="customerName" type="text" required>
                        </div>
                        <div class="col-12 col-md-6">
                            <label class="form-label" for="customerEmail">Customer Email</label>
                            <input class="form-control" id="customerEmail" name="customerEmail" type="email" required>
                        </div>
                    </div>

                    <div class="checkout-actions">
                        <a class="btn btn-outline-light" href="${pageContext.request.contextPath}/seats?showtimeId=<%= showtimeId %>">Back</a>
                        <button class="btn btn-danger" type="submit">Continue to Payment</button>
                    </div>
                </form>
            <% } %>
        </div>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
