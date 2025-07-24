<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error - Vendor Debtors</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .error-container {
            text-align: center;
            background: white;
            padding: 60px 40px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
            max-width: 500px;
            width: 90%;
        }
        
        .error-code {
            font-size: 120px;
            font-weight: bold;
            color: #ff6b6b;
            line-height: 1;
            margin-bottom: 20px;
        }
        
        .error-title {
            font-size: 28px;
            color: #333;
            margin-bottom: 15px;
            font-weight: 600;
        }
        
        .error-message {
            font-size: 16px;
            color: #666;
            margin-bottom: 30px;
            line-height: 1.6;
        }
        
        .error-actions {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        
        .btn {
            padding: 15px 30px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px rgba(255, 107, 107, 0.3);
        }
        
        .btn-secondary {
            background: #f8f9fa;
            color: #333;
            border: 2px solid #e9ecef;
        }
        
        .btn-secondary:hover {
            background: #e9ecef;
            transform: translateY(-1px);
        }
        
        .error-details {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 20px;
            margin: 20px 0;
            text-align: left;
            font-family: 'Courier New', monospace;
            font-size: 12px;
            color: #666;
            max-height: 200px;
            overflow-y: auto;
            display: none;
        }
        
        .toggle-details {
            color: #ff6b6b;
            cursor: pointer;
            font-size: 14px;
            margin-bottom: 10px;
        }
        
        .toggle-details:hover {
            text-decoration: underline;
        }
        
        @media (max-width: 480px) {
            .error-container {
                padding: 40px 20px;
            }
            
            .error-code {
                font-size: 80px;
            }
            
            .error-title {
                font-size: 24px;
            }
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-code">500</div>
        <h1 class="error-title">Internal Server Error</h1>
        <p class="error-message">
            Oops! Something went wrong on our server. Our team has been notified 
            and is working to fix this issue. Please try again later.
        </p>
        
        <% if (exception != null && request.getParameter("debug") != null) { %>
        <div class="toggle-details" onclick="toggleErrorDetails()">
            Show Error Details
        </div>
        <div class="error-details" id="errorDetails">
            <strong>Error Type:</strong> <%= exception.getClass().getSimpleName() %><br>
            <strong>Message:</strong> <%= exception.getMessage() != null ? exception.getMessage() : "No message available" %><br>
            <strong>Time:</strong> <%= new java.util.Date() %><br>
            <hr style="margin: 10px 0;">
            <% 
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                exception.printStackTrace(pw);
                String stackTrace = sw.toString();
                // Limit stack trace length for display
                if (stackTrace.length() > 2000) {
                    stackTrace = stackTrace.substring(0, 2000) + "...";
                }
            %>
            <%= stackTrace.replaceAll("\n", "<br>").replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;") %>
        </div>
        <% } %>
        
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Go to Dashboard</a>
            <a href="javascript:location.reload()" class="btn btn-secondary">Try Again</a>
        </div>
    </div>
    
    <script>
        function toggleErrorDetails() {
            const details = document.getElementById('errorDetails');
            const toggle = document.querySelector('.toggle-details');
            
            if (details.style.display === 'none' || details.style.display === '') {
                details.style.display = 'block';
                toggle.textContent = 'Hide Error Details';
            } else {
                details.style.display = 'none';
                toggle.textContent = 'Show Error Details';
            }
        }
    </script>
</body>
</html>