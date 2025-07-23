<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="${pageContext.request.contextPath}/">
    <meta charset="UTF-8">
    <title>Debt Management - Modern Dashboard</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/home.css">
</head>
<body>
    <div class="app-container">
        <main class="main-content">
            <div class="top-nav">
                <h1 class="page-title">Debt Management</h1>
                <nav class="nav-tabs">
                    <a href="logout" class="nav-tab">Logout</a>
                </nav>
            </div>

            <div id="home-content">
                <div class="dashboard-section">
                    <div class="main-card">
                        <div class="vendor-header">
                            <h2 class="vendor-name">${vendor.name}</h2>
                            <p class="vendor-subtitle">${vendor.username}</p>
                        </div>

                        <div class="stats-panel">
                            <div class="stat-card-main" style="--payment-percentage: ${stats.paymentPercentage}%;">
                                <div class="stat-label">Total Debt</div>
                                <div class="stat-value">$${stats.totalAmount}</div>
                            </div>

                            <div class="stat-card">
                                <div class="stat-label">Amount Owed</div>
                                <div class="stat-value">$${stats.totalBalance}</div>
                            </div>

                            <div class="stat-card">
                                <div class="stat-label">Overdue Amount</div>
                                <div class="stat-value">$${stats.totalOverdue}</div>
                            </div>

                            <div class="stats-actions">
                                <button id="view-detailed-stats" class="btn-stats" onclick="openStatsModal()">View Detailed Stats</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="data-section">
                    <div class="table-header">
                        <div class="table-header-left">
                            <h2 class="section-title">Active Debts</h2>
                        </div>
                        <div class="table-header-right">
                            <form method="GET" action="/" class="compact-search-form">
                                <input type="text" id="searchQuery" name="searchQuery" 
                                       value="${param.searchQuery}" 
                                       placeholder="Search by name or phone" 
                                       class="compact-search-input">
                                <select id="status" name="status" class="compact-status-filter">
                                    <option value="">All Status</option>
                                    <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="OVERDUE" ${param.status == 'OVERDUE' ? 'selected' : ''}>Overdue</option>
                                    <option value="PAID" ${param.status == 'PAID' ? 'selected' : ''}>Paid</option>
                                </select>
                                <button type="submit" class="compact-btn-search">Search</button>
                            </form>
                            <div class="action-buttons">
                                <a href="#add-debt-modal" class="btn-add-debt">Add New Debt</a>
                            </div>
                        </div>
                    </div>

                    <table class="debt-table">
                        <thead>
                            <tr>
                                <th>Debtor Name</th>
                                <th>Debtor Phone</th>
                                <th>Amount</th>
                                <th>Balance</th>
                                <th>Debt Date</th>
                                <th>Due Date</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="debt" items="${debts}">
                                <tr class="debt-row" 
                                    data-debt-id="${debt.id}"
                                    data-debtor-name="${debt.debtorName}"
                                    data-debtor-phone="${debt.debtorPhone}"
                                    data-amount="${debt.amount}"
                                    data-balance="${debt.balance}"
                                    data-description="${debt.description}"
                                    data-debt-date="${debt.debtDate}"
                                    data-due-date="${debt.dueDate}"
                                    data-created-date="${debt.createdDate}"
                                    data-status="${debt.status}">
                                    <td>${debt.debtorName}</td>
                                    <td>${debt.debtorPhone}</td>
                                    <td>${debt.amount}</td>
                                    <td>${debt.balance}</td>
                                    <td>${debt.debtDate}</td>
                                    <td>${debt.dueDate}</td>
                                    <td><span class="status-badge ${debt.status.toLowerCase()}">${debt.status}</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <div class="pagination-container">
                        <div class="pagination-info">
                            <c:choose>
                                <c:when test="${totalDebts > 0}">
                                    Showing ${startEntry}-${endEntry} of ${totalDebts} entries
                                </c:when>
                                <c:otherwise>
                                    No entries found
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="pagination-controls">
                            <c:if test="${hasPrevious}">
                                <a href="?page=${currentPage - 1}${not empty param.searchQuery ? '&searchQuery='.concat(param.searchQuery) : ''}${not empty param.status ? '&status='.concat(param.status) : ''}" class="pagination-btn">&larr;</a>
                            </c:if>
                            <c:if test="${!hasPrevious}">
                                <span class="pagination-btn disabled">&larr;</span>
                            </c:if>
                            
                            <c:forEach var="i" begin="1" end="${totalPages > 10 ? 10 : totalPages}">
                                <c:set var="pageNum" value="${currentPage <= 3 ? i : (currentPage + i - 3 <= totalPages ? currentPage + i - 3 : totalPages - 10 + i)}" />
                                <c:if test="${pageNum > 0 && pageNum <= totalPages}">
                                    <c:choose>
                                        <c:when test="${pageNum == currentPage}">
                                            <span class="pagination-btn active">${pageNum}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="?page=${pageNum}${not empty param.searchQuery ? '&searchQuery='.concat(param.searchQuery) : ''}${not empty param.status ? '&status='.concat(param.status) : ''}" class="pagination-btn">${pageNum}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:forEach>
                            
                            <c:if test="${hasNext}">
                                <a href="?page=${currentPage + 1}${not empty param.searchQuery ? '&searchQuery='.concat(param.searchQuery) : ''}${not empty param.status ? '&status='.concat(param.status) : ''}" class="pagination-btn">&rarr;</a>
                            </c:if>
                            <c:if test="${!hasNext}">
                                <span class="pagination-btn disabled">&rarr;</span>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <%@ include file="add_debt.jsp" %>
    <%@ include file="debt_info.jsp" %>
    <%@ include file="add_transaction.jsp" %>
    <%@ include file="detailed-stats.jsp" %>


    <script>
        function openStatsModal() {
            const statsModal = document.getElementById('detailed-stats-modal');
            if (statsModal) {
                statsModal.style.display = 'block';
            }
        }

        function closeStatsModal() {
            const statsModal = document.getElementById('detailed-stats-modal');
            if (statsModal) {
                statsModal.style.display = 'none';
            }
        }

        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.has('statsStartDate') || urlParams.has('statsEndDate')) {
                openStatsModal();
            }
        });
    </script>
    <script src="js/home.js"></script>
</body>
</html>