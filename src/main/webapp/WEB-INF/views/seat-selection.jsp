<%@ page import="com.moviebooking.model.Seat" %>
<%@ page import="com.moviebooking.model.SeatMap" %>
<%@ page import="com.moviebooking.model.SeatStatus" %>
<%@ page import="com.moviebooking.model.Showtime" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    Showtime showtime = (Showtime) request.getAttribute("showtime");
    SeatMap seatMap = (SeatMap) request.getAttribute("seatMap");
    String movieTitle = (String) request.getAttribute("movieTitle");
    Double seatPrice = (Double) request.getAttribute("seatPrice");
    ArrayList<Seat> seats = seatMap.getAllSeats();
%>

<main class="booking-page">
    <section class="container seat-page">
        <div class="booking-steps">
            <span>Pick a Movie</span>
            <span>Your Details</span>
            <span class="active">Pick a Seat</span>
            <span>Summary</span>
        </div>

        <div class="seat-layout">
            <section class="seat-panel">
                <span class="hero-kicker"><%= showtime.getCinemaHall() %> | <%= showtime.getDate() %> | <%= showtime.getTime() %></span>
                <h1><%= movieTitle %></h1>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
                <% } %>

                <div class="screen">Screen</div>

                <form method="post" action="${pageContext.request.contextPath}/seats" id="seatForm">
                    <input type="hidden" name="showtimeId" value="<%= showtime.getId() %>">
                    <div class="seat-grid">
                        <% for (Seat seat : seats) {
                            boolean isAvailable = SeatStatus.AVAILABLE.equals(seat.getStatus());
                            String statusClass = seat.getStatus().name().toLowerCase();
                        %>
                            <label class="seat-option <%= statusClass %>">
                                <input type="checkbox" name="selectedSeats" value="<%= seat.getSeatCode() %>" <%= isAvailable ? "" : "disabled" %>>
                                <span><%= seat.getSeatCode() %></span>
                            </label>
                        <% } %>
                    </div>

                    <div class="seat-legend">
                        <span><i class="legend-box available"></i>Available</span>
                        <span><i class="legend-box selected"></i>Selected</span>
                        <span><i class="legend-box held"></i>Held</span>
                        <span><i class="legend-box booked"></i>Booked</span>
                    </div>

                    <aside class="summary-panel">
                        <h2>Summary</h2>
                        <p>Selected seats: <strong id="selectedCount">0</strong></p>
                        <p>Seat price: LKR <%= String.format("%.2f", seatPrice) %></p>
                        <p>Total: <strong>LKR <span id="totalPrice">0.00</span></strong></p>
                        <button class="btn btn-danger w-100" type="submit">Proceed</button>
                    </aside>
                </form>
            </section>
        </div>
    </section>
</main>

<script>
    const seatPrice = <%= seatPrice %>;
    const selectedCount = document.getElementById('selectedCount');
    const totalPrice = document.getElementById('totalPrice');
    const seatInputs = document.querySelectorAll('.seat-option input[type="checkbox"]');

    function updateSeatSummary() {
        const selectedSeats = document.querySelectorAll('.seat-option input[type="checkbox"]:checked').length;
        selectedCount.textContent = selectedSeats;
        totalPrice.textContent = (selectedSeats * seatPrice).toFixed(2);
    }

    seatInputs.forEach((input) => {
        input.addEventListener('change', () => {
            input.closest('.seat-option').classList.toggle('selected', input.checked);
            updateSeatSummary();
        });
    });
</script>

<%@ include file="fragments/footer.jspf" %>
