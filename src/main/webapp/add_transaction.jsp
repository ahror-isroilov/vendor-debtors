<link rel="stylesheet" href="css/add_transaction.css">
<div id="add-transaction-modal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add Transaction</h2>
        </div>
        <div class="modal-body">
            <form id="add-transaction-form" action="debt-transactions" method="post">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="debtId" id="transaction-debt-id">

                <div class="form-group">
                    <label for="transaction-type">Transaction Type *</label>
                    <select id="transaction-type" name="transactionType" class="form-input" required>
                        <option value="">Select type...</option>
                        <option value="CREDIT">Credit</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="transaction-amount">Amount *</label>
                    <input type="number" id="transaction-amount" name="amount" class="form-input" step="0.01" min="0.01"
                           placeholder="0.00" required>
                    <small class="form-hint">Available balance: $<span id="available-balance">0.00</span></small>
                </div>

                <div class="form-group">
                    <label for="transaction-description">Description</label>
                    <textarea id="transaction-description" name="description" class="form-input" rows="3"
                              placeholder="Optional description..."></textarea>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn-secondary" onclick="resetTransactionForm()">Cancel</a>
            <button type="submit" form="add-transaction-form" class="btn-primary">Add Transaction</button>
        </div>
    </div>
</div>

<script>
function resetTransactionForm() {
    const form = document.getElementById('add-transaction-form');
    if (form) {
        form.reset();
        // Remove validation classes
        form.querySelectorAll('.form-input').forEach(input => {
            input.classList.remove('was-validated');
        });
        form.querySelectorAll('.form-group').forEach(group => {
            group.classList.remove('error');
        });
    }
}

// Add validation classes on form submit attempt
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('add-transaction-form');
    if (form) {
        form.addEventListener('submit', function(e) {
            // Add validation class to trigger CSS validation styles
            form.querySelectorAll('.form-input').forEach(input => {
                input.classList.add('was-validated');
            });
        });
        
        // Reset form when modal is closed
        document.addEventListener('click', function(e) {
            if (e.target.matches('a[href="#"]') || e.target.closest('.modal')) {
                if (!e.target.closest('.modal-content')) {
                    resetTransactionForm();
                }
            }
        });
    }
});
</script>

