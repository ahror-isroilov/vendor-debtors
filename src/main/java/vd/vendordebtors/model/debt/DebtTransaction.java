package vd.vendordebtors.model.debt;

import java.math.BigDecimal;
import java.util.Date;

public class DebtTransaction {
    private int id;
    private int debtId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private Date createdDate;
    private String status;

    public DebtTransaction() {
    }

    public DebtTransaction(int debtId, String transactionType, BigDecimal amount, String status) {
        this.debtId = debtId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDebtId() {
        return debtId;
    }

    public void setDebtId(int debtId) {
        this.debtId = debtId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
