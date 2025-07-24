<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ page import="java.io.*, java.util.Date" %>
<!DOCTYPE html>
<html lang="en" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error - Vendor Debtors</title>
    <style>
        :root {
            --background: 222.2 84% 4.9%;
            --foreground: 210 40% 98%;
            --card: 222.2 84% 4.9%;
            --card-foreground: 210 40% 98%;
            --popover: 222.2 84% 4.9%;
            --popover-foreground: 210 40% 98%;
            --primary: 210 40% 98%;
            --primary-foreground: 222.2 84% 4.9%;
            --secondary: 217.2 32.6% 17.5%;
            --secondary-foreground: 210 40% 98%;
            --muted: 217.2 32.6% 17.5%;
            --muted-foreground: 215 20.2% 65.1%;
            --accent: 217.2 32.6% 17.5%;
            --accent-foreground: 210 40% 98%;
            --destructive: 0 84.2% 60.2%;
            --destructive-foreground: 210 40% 98%;
            --border: 217.2 32.6% 17.5%;
            --input: 217.2 32.6% 17.5%;
            --ring: 212.7 26.8% 83.9%;
            --radius: 0.5rem;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            background-color: hsl(var(--background));
            color: hsl(var(--foreground));
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }

        .error-container {
            background-color: hsl(var(--card));
            border: 1px solid hsl(var(--border));
            border-radius: var(--radius);
            padding: 2rem;
            width: 100%;
            max-width: 32rem;
            text-align: center;
        }

        .error-icon {
            width: 4rem;
            height: 4rem;
            margin: 0 auto 1.5rem;
            border-radius: 50%;
            background-color: hsl(var(--destructive) / 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            border: 1px solid hsl(var(--destructive) / 0.2);
        }

        .error-code {
            font-size: 1.5rem;
            font-weight: 600;
            color: hsl(var(--destructive));
        }

        .error-title {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: hsl(var(--foreground));
        }

        .error-message {
            color: hsl(var(--muted-foreground));
            margin-bottom: 2rem;
            font-size: 0.875rem;
            line-height: 1.5;
        }

        .error-actions {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
            margin-bottom: 1.5rem;
        }

        .btn {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: var(--radius);
            font-size: 0.875rem;
            font-weight: 500;
            height: 2.5rem;
            padding: 0 1rem;
            text-decoration: none;
            transition: all 0.2s;
            border: none;
            cursor: pointer;
        }

        .btn-primary {
            background-color: hsl(var(--primary));
            color: hsl(var(--primary-foreground));
        }

        .btn-primary:hover {
            background-color: hsl(var(--primary) / 0.9);
        }

        .btn-ghost {
            color: hsl(var(--foreground));
        }

        .btn-ghost:hover {
            background-color: hsl(var(--accent));
            color: hsl(var(--accent-foreground));
        }

        .btn-outline {
            border: 1px solid hsl(var(--border));
            background-color: transparent;
            color: hsl(var(--foreground));
        }

        .btn-outline:hover {
            background-color: hsl(var(--accent));
            color: hsl(var(--accent-foreground));
        }

        .collapsible {
            margin-top: 1rem;
        }

        .collapsible-trigger {
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0.75rem;
            background-color: hsl(var(--secondary));
            border: 1px solid hsl(var(--border));
            border-radius: var(--radius);
            color: hsl(var(--secondary-foreground));
            font-size: 0.875rem;
            cursor: pointer;
            transition: all 0.2s;
        }

        .collapsible-trigger:hover {
            background-color: hsl(var(--secondary) / 0.8);
        }

        .collapsible-content {
            display: none;
            margin-top: 0.5rem;
            background-color: hsl(var(--muted));
            border: 1px solid hsl(var(--border));
            border-radius: var(--radius);
            padding: 1rem;
            max-height: 16rem;
            overflow-y: auto;
        }

        .error-details {
            font-family: ui-monospace, SFMono-Regular, "SF Mono", Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
            font-size: 0.75rem;
            color: hsl(var(--muted-foreground));
            text-align: left;
            line-height: 1.4;
            white-space: pre-wrap;
        }

        .error-meta {
            display: grid;
            gap: 0.5rem;
            margin-bottom: 1rem;
            padding: 0.75rem;
            background-color: hsl(var(--secondary) / 0.5);
            border-radius: var(--radius);
            font-size: 0.75rem;
        }

        .error-meta-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .error-meta-label {
            font-weight: 500;
            color: hsl(var(--muted-foreground));
        }

        .error-meta-value {
            color: hsl(var(--foreground));
            font-family: ui-monospace, SFMono-Regular, monospace;
        }

        .chevron {
            transition: transform 0.2s;
        }

        .chevron.open {
            transform: rotate(180deg);
        }

        @media (min-width: 640px) {
            .error-actions {
                flex-direction: row;
                justify-content: center;
            }
        }

        @media (max-width: 480px) {
            .error-container {
                padding: 1.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">
            <div class="error-code">500</div>
        </div>
        <h1 class="error-title">Internal server error</h1>
        <p class="error-message">
            Something went wrong on our end. We've been notified and are working to fix this issue.
        </p>
        
        <div class="error-actions">
            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Go home</a>
            <a href="javascript:location.reload()" class="btn btn-ghost">Try again</a>
        </div>

        <% 
        if (exception != null) {
            String errorType = exception.getClass().getSimpleName();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            String stackTrace = sw.toString();
            
            Throwable rootCause = exception;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            String rootCauseMessage = rootCause.getMessage();
            if (rootCauseMessage == null) rootCauseMessage = rootCause.getClass().getSimpleName();
        %>
        
        <div class="collapsible">
            <button class="collapsible-trigger" onclick="toggleErrorDetails()">
                <span>View error details</span>
                <svg class="chevron" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6,9 12,15 18,9"></polyline>
                </svg>
            </button>
            
            <div class="collapsible-content" id="errorDetails">
                <div class="error-meta">
                    <div class="error-meta-item">
                        <span class="error-meta-label">Error Type:</span>
                        <span class="error-meta-value"><%= errorType %></span>
                    </div>
                    <div class="error-meta-item">
                        <span class="error-meta-label">Root Cause:</span>
                        <span class="error-meta-value"><%= rootCauseMessage %></span>
                    </div>
                    <div class="error-meta-item">
                        <span class="error-meta-label">Timestamp:</span>
                        <span class="error-meta-value"><%= new Date().toString() %></span>
                    </div>
                    <div class="error-meta-item">
                        <span class="error-meta-label">Request URI:</span>
                        <span class="error-meta-value"><%= request.getRequestURI() %></span>
                    </div>
                </div>
                
                <div class="error-details"><%= stackTrace %></div>
            </div>
        </div>
        
        <% } %>
    </div>
    
    <script>
        function toggleErrorDetails() {
            const content = document.getElementById('errorDetails');
            const chevron = document.querySelector('.chevron');
            
            if (content.style.display === 'none' || content.style.display === '') {
                content.style.display = 'block';
                chevron.classList.add('open');
            } else {
                content.style.display = 'none';
                chevron.classList.remove('open');
            }
        }
    </script>
</body>
</html>