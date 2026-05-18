<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>

<main class="auth-page">
    <section class="auth-card">
        <div class="auth-brand">CineFlex</div>
        <h1>Sign Up</h1>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/signup">
            <div class="mb-3">
                <label class="form-label" for="name">Full Name</label>
                <input class="form-control" id="name" name="name" type="text" value="${name}" required>
            </div>

            <div class="mb-3">
                <label class="form-label" for="username">Username</label>
                <input class="form-control" id="username" name="username" type="text" value="${username}" required>
            </div>

            <div class="mb-3">
                <label class="form-label" for="email">Email</label>
                <input class="form-control" id="email" name="email" type="email" value="${email}" required>
            </div>

            <div class="mb-3">
                <label class="form-label" for="password">Password</label>
                <input class="form-control" id="password" name="password" type="password" required>
            </div>

            <div class="mb-3">
                <label class="form-label" for="confirmPassword">Confirm Password</label>
                <input class="form-control" id="confirmPassword" name="confirmPassword" type="password" required>
            </div>

            <button class="btn btn-danger w-100" type="submit">Sign Up</button>
        </form>

        <p class="auth-link">Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a></p>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
