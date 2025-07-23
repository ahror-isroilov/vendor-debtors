<link rel="stylesheet" href="css/debt_info.css">
<div id="debt-info-modal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Debt Information</h2>
        </div>
        <div class="modal-body">
            <div class="tab-navigation">
                <button class="tab-btn active" data-tab="info">Debt Info</button>
                <button class="tab-btn" data-tab="transactions">Transactions</button>
                <button class="tab-btn" data-tab="edit">Edit</button>
            </div>

            <div id="info-tab" class="tab-content active">
                <div class="debt-summary-card">
                    <div class="debt-header">
                        <h3 id="info-debtor-name">-</h3>
                        <span class="status-badge" id="info-status">-</span>
                    </div>
                    <div class="debt-amounts">
                        <div class="amount-item">
                            <span class="amount-label">Original</span>
                            <span class="amount-value" id="info-amount">$0.00</span>
                        </div>
                        <div class="amount-item">
                            <span class="amount-label">Balance</span>
                            <span class="amount-value balance-highlight" id="info-balance">$0.00</span>
                        </div>
                        <div class="amount-item">
                            <span class="amount-label">Paid</span>
                            <span class="amount-value paid-amount" id="info-paid-amount">$0.00</span>
                        </div>
                    </div>
                </div>

                <div class="info-details">
                    <div class="detail-row">
                        <span class="detail-label">Phone:</span>
                        <span id="info-debtor-phone">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Debt Date:</span>
                        <span id="info-debt-date">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Due Date:</span>
                        <span id="info-due-date">-</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Created:</span>
                        <span id="info-created-date">-</span>
                    </div>
                </div>

                <div class="description-section">
                    <label>Description</label>
                    <div class="description-value" id="info-description">No description provided</div>
                </div>
            </div>

            <div id="transactions-tab" class="tab-content">
                <div class="transactions-header">
                    <div>
                        <label>Transaction History</label>
                        <span class="transaction-count" id="transaction-count">(0 transactions)</span>
                    </div>
                    <button class="btn-primary btn-sm" id="add-transaction-btn">+ Add Transaction</button>
                </div>
                <div class="transactions-list" id="transactions-container">
                    <div class="loading-transactions">Loading transactions...</div>
                </div>
            </div>

            <div id="edit-tab" class="tab-content">
                <form id="edit-debt-form" action="debt" method="post">
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="debtId" id="edit-debt-id">

                <div class="form-group">
                    <label for="edit-debtor-name">Debtor Name *</label>
                    <input type="text" id="edit-debtor-name" name="debtorName" class="form-input" required>
                </div>

                <div class="form-group">
                    <label for="edit-debtor-phone">Phone Number</label>
                    <input type="tel" id="edit-debtor-phone" name="debtorPhone" class="form-input">
                </div>

                <div class="form-group">
                    <label for="edit-amount">Amount *</label>
                    <input type="number" id="edit-amount" name="amount" class="form-input" step="0.01" min="0" required>
                </div>

                <div class="form-group">
                    <label for="edit-balance">Balance *</label>
                    <input type="number" id="edit-balance" name="balance" class="form-input" step="0.01" min="0" required>
                </div>

                <div class="form-group">
                    <label for="edit-debt-date">Debt Date *</label>
                    <input type="date" id="edit-debt-date" name="debtDate" class="form-input" required>
                </div>

                <div class="form-group">
                    <label for="edit-due-date">Due Date</label>
                    <input type="date" id="edit-due-date" name="dueDate" class="form-input">
                </div>

                <div class="form-group">
                    <label for="edit-status">Status</label>
                    <select id="edit-status" name="status" class="form-input">
                        <option value="PENDING">Pending</option>
                        <option value="PAID">Paid</option>
                        <option value="OVERDUE">Overdue</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="edit-description">Description</label>
                    <textarea id="edit-description" name="description" class="form-input" rows="3"></textarea>
                </div>
                </form>
            </div>
        </div>
        <div class="modal-footer">
            <div class="footer-actions">
                <form id="delete-debt-form" action="debt" method="post" style="display: inline;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="debtId" id="delete-debt-id">
                    <button type="submit" class="btn-danger" id="delete-debt-btn">Delete</button>
                </form>
                <div class="footer-right">
                    <a href="#" class="btn-secondary">Close</a>
                    <button type="submit" form="edit-debt-form" class="btn-primary" id="save-debt-btn">Save Changes</button>
                </div>
            </div>
        </div>
    </div>
</div>

