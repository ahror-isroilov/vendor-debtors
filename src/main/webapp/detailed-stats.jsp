<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="css/detailed_stats.css">
<div id="detailed-stats-modal" class="modal" style="display: none;">
    <div class="modal-content stats-modal">
        <div class="modal-header">
            <h2>Detailed Statistics</h2>
        </div>
        <div class="modal-body">
            <form method="GET" action="/home" class="stats-form">
                <div class="date-range-selector">
                    <div class="form-group">
                        <label for="stats-start-date">From:</label>
                        <input type="date" id="stats-start-date" name="statsStartDate"
                               value="${param.statsStartDate != null ? param.statsStartDate : defaultStartDate}"
                               class="form-input">
                    </div>

                    <div class="form-group">
                        <label for="stats-end-date">To:</label>
                        <input type="date" id="stats-end-date" name="statsEndDate"
                               value="${param.statsEndDate != null ? param.statsEndDate : defaultEndDate}"
                               class="form-input">
                    </div>

                    <button type="submit" class="btn-load-stats">Load</button>
                </div>
            </form>

            <c:if test="${detailedStats != null}">
                <div class="detailed-stats-grid">
                    <div class="detailed-stat-card">
                        <div class="stat-label">Total Debts</div>
                        <div class="stat-value">${detailedStats.totalDebts}</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Total Amount</div>
                        <div class="stat-value">$${detailedStats.totalAmount}</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Total Balance</div>
                        <div class="stat-value">$${detailedStats.totalBalance}</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Payment percentage</div>
                        <div class="stat-value">${detailedStats.paymentPercentage}%</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Average Balance</div>
                        <div class="stat-value">$${detailedStats.averageBalance}</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Pending Debts</div>
                        <div class="stat-value">${detailedStats.totalPending}</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Paid Debts</div>
                        <div class="stat-value">${detailedStats.totalPaid}</div>
                    </div>
                    <div class="detailed-stat-card">
                        <div class="stat-label">Overdue Debts</div>
                        <div class="stat-value highlight-red">${detailedStats.totalOverdue}</div>
                    </div>
                </div>
            </c:if>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-primary" onclick="window.open('export', '_blank')">Export Excel</button>
            <button type="button" class="btn-secondary" onclick="closeStatsModal()">Close</button>
        </div>
    </div>
</div>
