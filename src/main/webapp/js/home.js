document.addEventListener('DOMContentLoaded', function() {
    const debtDateInput = document.getElementById('debtDate');
    if (debtDateInput) {
        debtDateInput.value = new Date().toISOString().split('T')[0];
    }
    
    document.querySelectorAll('.debt-row').forEach(row => {
        row.addEventListener('click', function() {
            document.querySelectorAll('.debt-row').forEach(r => r.classList.remove('selected'));
            this.classList.add('selected');
            openDebtInfoModal(this);
        });
    });
    
    const addTransactionBtn = document.getElementById('add-transaction-btn');
    if (addTransactionBtn) {
        addTransactionBtn.addEventListener('click', openAddTransactionModal);
    }
    
    document.addEventListener('click', function(e) {
        if (e.target.matches('.btn-secondary')) {
            e.preventDefault();
            if (e.target.closest('#debt-info-modal')) {
                closeDebtInfoModal();
            } else if (e.target.closest('#add-debt-modal')) {
                window.location.hash = '';
            } else if (e.target.closest('#add-transaction-modal')) {
                window.location.hash = '';
            }
        }
    });
    
    const debtInfoModal = document.getElementById('debt-info-modal');
    if (debtInfoModal) {
        debtInfoModal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeDebtInfoModal();
            }
        });
    }
});

function openDebtInfoModal(row) {
    const debtData = row.dataset;
    
    window.currentDebtData = debtData;
    
    document.getElementById('info-debtor-name').textContent = debtData.debtorName || '-';
    document.getElementById('info-debtor-phone').textContent = debtData.debtorPhone || '-';
    document.getElementById('info-amount').textContent = '$' + (debtData.amount || '0.00');
    document.getElementById('info-balance').textContent = '$' + (debtData.balance || '0.00');
    document.getElementById('info-debt-date').textContent = debtData.debtDate || '-';
    document.getElementById('info-due-date').textContent = debtData.dueDate || '-';
    document.getElementById('info-created-date').textContent = debtData.createdDate || '-';
    document.getElementById('info-description').textContent = debtData.description || 'No description';
    
    const statusElement = document.getElementById('info-status');
    const status = debtData.status || 'PENDING';
    statusElement.textContent = status;
    statusElement.className = 'status-badge ' + status.toLowerCase();
    
    const amount = parseFloat(debtData.amount || 0);
    const balance = parseFloat(debtData.balance || 0);
    const paidAmount = amount - balance;
    
    document.getElementById('info-paid-amount').textContent = '$' + paidAmount.toFixed(2);
    
    const addTransactionBtn = document.getElementById('add-transaction-btn');
    if (status === 'PAID' || balance <= 0) addTransactionBtn.style.display = 'none'; else addTransactionBtn.style.display = 'inline-block';

    populateForms(debtData);
    
    loadDebtTransactions(debtData.debtId);
    
    switchTab('info');
    setupTabListeners();
    
    document.getElementById('debt-info-modal').classList.add('show');
}

function closeDebtInfoModal() {
    document.getElementById('debt-info-modal').classList.remove('show');
    document.querySelectorAll('.debt-row').forEach(r => r.classList.remove('selected'));
}

function populateForms(debtData) {
    document.getElementById('edit-debt-id').value = debtData.debtId || '';
    document.getElementById('delete-debt-id').value = debtData.debtId || '';
    document.getElementById('edit-debtor-name').value = debtData.debtorName || '';
    document.getElementById('edit-debtor-phone').value = debtData.debtorPhone || '';
    document.getElementById('edit-amount').value = debtData.amount || '';
    document.getElementById('edit-balance').value = debtData.balance || '';
    document.getElementById('edit-debt-date').value = debtData.debtDate || '';
    document.getElementById('edit-due-date').value = debtData.dueDate || '';
    document.getElementById('edit-status').value = debtData.status || 'PENDING';
    document.getElementById('edit-description').value = debtData.description || '';
}

function switchTab(tabName) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
    
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(`${tabName}-tab`).classList.add('active');
    
    const deleteBtn = document.getElementById('delete-debt-btn');
    const saveBtn = document.getElementById('save-debt-btn');
    
    if (tabName === 'edit') {
        deleteBtn.style.display = 'inline-block';
        saveBtn.style.display = 'inline-block';
    } else {
        deleteBtn.style.display = 'none';
        saveBtn.style.display = 'none';
    }
}

function setupTabListeners() {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.replaceWith(btn.cloneNode(true));
    });
    
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            switchTab(this.dataset.tab);
        });
    });
}

function loadDebtTransactions(debtId) {
    if (!debtId) return;
    
    const transactionsContainer = document.getElementById('transactions-container');
    const transactionCount = document.getElementById('transaction-count');
    
    transactionsContainer.innerHTML = '<div class="loading-transactions">Loading transactions...</div>';
    transactionCount.textContent = '(Loading...)';
    
    fetch(`debt-transactions?debtId=${debtId}`)
        .then(response => response.ok ? response.json() : Promise.reject('Failed to load'))
        .then(transactions => displayTransactions(transactions))
        .catch(error => {
            console.error('Error loading transactions:', error);
            transactionsContainer.innerHTML = '<div class="no-transactions">Failed to load transactions</div>';
            transactionCount.textContent = '(Error)';
        });
}

function displayTransactions(transactions) {
    const transactionsContainer = document.getElementById('transactions-container');
    const transactionCount = document.getElementById('transaction-count');
    
    if (transactions.length === 0) {
        transactionsContainer.innerHTML = '<div class="no-transactions">No transactions found</div>';
        transactionCount.textContent = '(0 transactions)';
        return;
    }
    
    let transactionsHtml = '';
    transactions.forEach(transaction => {
        const amount = parseFloat(transaction.amount || 0).toFixed(2);
        const date = new Date(transaction.createdDate).toLocaleDateString();
        const status = transaction.status || 'FAIL';
        const statusClass = status === 'SUCCESS' ? 'status-success' : 'status-fail';
        const type = (transaction.transactionType || '').toLowerCase().replace('_', ' ');
        const description = transaction.description || '';
        
        transactionsHtml += `
            <div class="transaction-item">
                <div class="transaction-main">
                    <div>
                        <span class="transaction-amount">$${amount}</span>
                        <span class="transaction-type"> - ${type}</span>
                        <span class="transaction-date"> (${date})</span>
                    </div>
                    <div class="transaction-status">
                        <div class="status-indicator ${statusClass}"></div>
                        <form method="POST" action="debt-transactions" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="transactionId" value="${transaction.id}">
                            <button type="submit" class="btn-delete-transaction" title="Delete Transaction">×</button>
                        </form>
                    </div>
                </div>
                ${description ? `<div class="transaction-description">${description}</div>` : ''}
            </div>
        `;
    });
    
    transactionsContainer.innerHTML = transactionsHtml;
    transactionCount.textContent = `(${transactions.length} transaction${transactions.length !== 1 ? 's' : ''})`; 
}

function openAddTransactionModal() {
    if (!window.currentDebtData) return;
    
    const debtData = window.currentDebtData;
    document.getElementById('transaction-debt-id').value = debtData.debtId;
    document.getElementById('available-balance').textContent = parseFloat(debtData.balance || 0).toFixed(2);
    
    const amountInput = document.getElementById('transaction-amount');
    amountInput.max = debtData.balance;
    
    window.location.hash = 'add-transaction-modal';
}

