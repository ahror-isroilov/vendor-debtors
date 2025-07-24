<link rel="stylesheet" href="css/add_debt.css">
<div id="add-debt-modal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>Add New Debt</h2>
        </div>
        <div class="modal-body">
            <form id="add-debt-form" action="debt" method="post">
                <input type="hidden" name="action" value="add">
                
                <div class="form-group">
                    <label for="debtorName">Debtor Name *</label>
                    <input type="text" id="debtorName" name="debtorName" class="form-input" placeholder="Enter debtor's name" required>
                </div>
                
                <div class="form-group">
                    <label for="debtorPhone">Phone Number</label>
                    <input type="tel" id="debtorPhone" name="debtorPhone" class="form-input" placeholder="Enter phone number">
                </div>
                
                <div class="form-group">
                    <label for="amount">Amount *</label>
                    <input type="number" id="amount" name="amount" class="form-input" step="0.01" min="0" placeholder="0.00" required>
                </div>
                
                <div class="form-group">
                    <label for="debtDate">Debt Date *</label>
                    <input type="date" id="debtDate" name="debtDate" class="form-input" required>
                </div>
                
                <div class="form-group">
                    <label for="dueDate">Due Date</label>
                    <input type="date" id="dueDate" name="dueDate" class="form-input">
                </div>
                
                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" class="form-input" rows="3"></textarea>
                </div>
            </form>
        </div>
        <div class="modal-footer">
            <a href="#" class="btn-secondary" onclick="resetForm()">Cancel</a>
            <button type="submit" form="add-debt-form" class="btn-primary">Add Debt</button>
        </div>
    </div>
</div>

<script>
function resetForm() {
    const form = document.getElementById('add-debt-form');
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
    const form = document.getElementById('add-debt-form');
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
                    resetForm();
                }
            }
        });
    }
});
</script>