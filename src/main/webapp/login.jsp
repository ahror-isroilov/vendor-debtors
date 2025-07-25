<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <div class="login-header">
            <h1>Welcome</h1>
            <p>Enter your credentials to access your account</p>
        </div>

        <form action="login" method="post" class="login-form">
            <div class="form-group">
                <label for="username">Username</label>
                <input
                        type="text"
                        id="username"
                        name="username"
                        class="form-input"
                        placeholder="Enter your username"
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
                        placeholder="Enter your password"
                        required
                />
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
            <% } %>

            <div class="form-actions">
                <button type="submit" name="action" value="login" class="btn btn-primary">
                    Sign In
                </button>
                <a href="register.jsp" class="btn btn-secondary">
                    Create Account
                </a>
            </div>
        </form>
    </div>
</div>
</body>
</html>