<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Vendor Registration</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <h1>Create Account</h1>
            <p>Create an account and start managing your debts</p>
        </div>

        <form action="register" method="post" class="login-form">
            <div class="form-group">
                <label for="name">Full Name</label>
                <input
                        type="text"
                        id="name"
                        name="name"
                        class="form-input"
                        placeholder="Enter your full name"
                        value="<%= request.getParameter("name") != null ? request.getParameter("name") : "" %>"
                        required
                />
            </div>

            <div class="form-group">
                <label for="username">Username</label>
                <input
                        type="text"
                        id="username"
                        name="username"
                        class="form-input"
                        placeholder="Choose a username"
                        value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>"
                        required
                />
            </div>

            <div class="form-group">
                <label for="phone">Phone Number</label>
                <input
                        type="tel"
                        id="phone"
                        name="phone"
                        class="form-input"
                        placeholder="Enter your phone number"
                        value="<%= request.getParameter("phone") != null ? request.getParameter("phone") : "" %>"
                        required
                />
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input
                        type="password"
                        id="password"
                        name="password"
                        class="form-input"
                        placeholder="Create a password"
                        required
                />
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        class="form-input"
                        placeholder="Confirm your password"
                        required
                />
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
            <div class="success-message">
                <%= request.getAttribute("success") %>
            </div>
            <% } %>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    Create Account
                </button>
                <a href="login.jsp" class="btn btn-secondary">
                    Already have an account?
                </a>
            </div>
        </form>
    </div>
</div>
</body>
</html>