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

<style>
.tab-navigation {
    display: flex;
    border-bottom: 2px solid #e5e7eb;
    margin-bottom: 1.5rem;
}

.tab-btn {
    padding: 0.75rem 1.5rem;
    background: none;
    border: none;
    font-size: 0.875rem;
    font-weight: 500;
    color: #6b7280;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    transition: all 0.2s ease;
}

.tab-btn:hover {
    color: #3b82f6;
}

.tab-btn.active {
    color: #3b82f6;
    border-bottom-color: #3b82f6;
}

.tab-content {
    display: none;
}

.tab-content.active {
    display: block;
}

.debt-summary-card {
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    padding: 1.5rem;
    border-radius: 12px;
    margin-bottom: 1.5rem;
    border: 1px solid #cbd5e1;
}

.debt-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.debt-header h3 {
    margin: 0;
    font-size: 1.25rem;
    font-weight: 600;
    color: #1e293b;
}

.debt-amounts {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1rem;
}

.amount-item {
    text-align: center;
}

.amount-label {
    display: block;
    font-size: 0.75rem;
    font-weight: 500;
    color: #64748b;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.25rem;
}

.amount-value {
    display: block;
    font-size: 1.125rem;
    font-weight: 700;
    color: #1e293b;
}

.info-details {
    background: white;
    padding: 1rem;
    border-radius: 8px;
    border: 1px solid #e5e7eb;
    margin-bottom: 1.5rem;
}

.detail-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    border-bottom: 1px solid #f3f4f6;
}

.detail-row:last-child {
    border-bottom: none;
}

.detail-label {
    font-weight: 500;
    color: #6b7280;
    font-size: 0.875rem;
}

.btn-sm {
    padding: 0.5rem 1rem;
    font-size: 0.875rem;
}

.transactions-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.transactions-list {
    max-height: 250px;
    overflow-y: auto;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    background: white;
}

.loading-transactions {
    padding: 2rem;
    text-align: center;
    color: #6b7280;
    font-style: italic;
}

.no-transactions {
    padding: 2rem;
    text-align: center;
    color: #9ca3af;
}

.transaction-item {
    padding: 0.875rem;
    border-bottom: 1px solid #f3f4f6;
    transition: background-color 0.2s ease;
}

.transaction-item:last-child {
    border-bottom: none;
}

.transaction-item:hover {
    background-color: #f9fafb;
}

.transaction-main {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.25rem;
}

.transaction-amount {
    font-weight: 600;
    font-size: 0.95rem;
    color: #1f2937;
}

.transaction-type {
    font-size: 0.825rem;
    color: #6b7280;
    text-transform: capitalize;
}

.transaction-date {
    font-size: 0.75rem;
    color: #9ca3af;
}

.transaction-status {
    display: flex;
    align-items: center;
    gap: 0.375rem;
}

.status-indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    flex-shrink: 0;
}

.status-success {
    background-color: #10b981;
}

.status-fail {
    background-color: #ef4444;
}

.btn-delete-transaction {
    background: #ef4444;
    color: white;
    border: none;
    border-radius: 4px;
    width: 20px;
    height: 20px;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-left: 8px;
    transition: background-color 0.2s ease;
}

.btn-delete-transaction:hover {
    background: #dc2626;
}

.transaction-description {
    font-size: 0.75rem;
    color: #6b7280;
    margin-top: 0.25rem;
    font-style: italic;
}
</style>