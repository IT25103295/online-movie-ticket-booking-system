<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="fragments/header.jspf" %>

<main class="auth-page">
    <section class="auth-card">
        <div class="auth-brand">CineFlex</div>
        <h1>Login</h1>

        <% if (request.getParameter("registered") != null) { %>
            <div class="alert alert-success" role="alert">Account created. Please log in.</div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger" role="alert"><%= request.getAttribute("error") %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="mb-3">
                <label class="form-label" for="usernameOrEmail">Username or Email</label>
                <input class="form-control" id="usernameOrEmail" name="usernameOrEmail" type="text"
                       value="${usernameOrEmail}" required>
            </div>

            <div class="mb-3">
                <label class="form-label" for="password">Password</label>
                <input class="form-control" id="password" name="password" type="password" required>
            </div>

            <button class="btn btn-danger w-100" type="submit">Login</button>
        </form>

        <div class="demo-hint">
            <strong>Demo credentials</strong><br>
            admin: admin / admin123<br>
            user: user / user123
        </div>

        <p class="auth-link">New to CineFlex? <a href="${pageContext.request.contextPath}/signup">Create an account</a></p>
    </section>
</main>

<%@ include file="fragments/footer.jspf" %>
