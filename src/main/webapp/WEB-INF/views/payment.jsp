<%@ page import="com.moviebooking.model.Payment" %>
<%@ page import="com.moviebooking.model.PaymentMethod" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>
<%
    ArrayList<String> selectedSeats = (ArrayList<String>) request.getAttribute("selectedSeats");
    PaymentMethod[] paymentMethods = (PaymentMethod[]) request.getAttribute("paymentMethods");
    Payment failedPayment = (Payment) request.getAttribute("payment");
    Double totalAmount = (Double) request.getAttribute("totalAmount");
%>

<main class="booking-page payment-page">
    <section class="container checkout-wrap">
        <div class="payment-card">
            <span class="hero-kicker">Demo Payment</span>
            <h1>Payment Method</h1>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger" role="alert">
                    <%= request.getAttribute("error") %>
                    <% if (failedPayment != null) { %>
                        <br>Payment ID: <strong><%= failedPayment.getPaymentId() %></strong>
                    <% } %>
                </div>
            <% } %>

            <div class="summary-grid payment-summary">
                <div class="summary-item"><span>Movie</span><strong><%= request.getAttribute("movieLabel") %></strong></div>
                <div class="summary-item"><span>Showtime</span><strong><%= request.getAttribute("showtimeId") %></strong></div>
                <div class="summary-item"><span>Seats</span><strong><%= String.join(", ", selectedSeats) %></strong></div>
                <div class="summary-item"><span>Customer</span><strong><%= request.getAttribute("customerName") %></strong></div>
                <div class="summary-item total"><span>Total Amount</span><strong>LKR <%= String.format("%.2f", totalAmount) %></strong></div>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/payment" class="checkout-form payment-form">
                <div class="mb-3">
                    <label class="form-label" for="paymentMethod">Payment Method</label>
                    <select class="form-select" id="paymentMethod" name="paymentMethod">
                        <% for (PaymentMethod method : paymentMethods) { %>
                            <option value="<%= method.name() %>"><%= method.name().replace("_", " ") %></option>
                        <% } %>
                    </select>
                </div>

                <div id="cardFields" class="card-fields">
                    <div class="row g-3">
                        <div class="col-12 col-md-6">
                            <label class="form-label" for="cardHolderName">Card Holder Name</label>
                            <input class="form-control" id="cardHolderName" name="cardHolderName" type="text">
                        </div>
                        <div class="col-12 col-md-6">
                            <label class="form-label" for="cardNumber">Card Number</label>
                            <input class="form-control" id="cardNumber" name="cardNumber" type="text" maxlength="19" placeholder="4111111111111111">
                        </div>
                        <div class="col-12 col-md-6">
                            <label class="form-label" for="expiryDate">Expiry Date</label>
                            <input class="form-control" id="expiryDate" name="expiryDate" type="text" placeholder="MM/YY">
                        </div>
                        <div class="col-12 col-md-6">
                            <label class="form-label" for="cvv">CVV</label>
                            <input class="form-control" id="cvv" name="cvv" type="password" maxlength="4">
                        </div>
                    </div>
                    <p class="demo-hint">Demo rule: card numbers ending in 0000 fail. No real payment is processed.</p>
                </div>

                <div class="checkout-actions">
                    <a class="btn btn-outline-light" href="${pageContext.request.contextPath}/checkout">Back</a>
                    <button class="btn btn-danger" type="submit">Pay & Confirm Booking</button>
                </div>
            </form>
        </div>
    </section>
</main>

<script>
    const paymentMethod = document.getElementById('paymentMethod');
    const cardFields = document.getElementById('cardFields');

    function toggleCardFields() {
        const value = paymentMethod.value;
        cardFields.style.display = value === 'CREDIT_CARD' || value === 'DEBIT_CARD' ? 'block' : 'none';
    }

    paymentMethod.addEventListener('change', toggleCardFields);
    toggleCardFields();
</script>

<%@ include file="fragments/footer.jspf" %>
