package vd.vendordebtors.model.debt;

import java.math.BigDecimal;
import java.util.Date;

public class Debt {
    private int id;
    private int vendorId;
    private String debtorName;
    private String debtorPhone;
    private BigDecimal amount;
    private BigDecimal balance;
    private String description;
    private Date debtDate;
    private Date dueDate;
    private Date createdDate;
    private String status;

    public Debt() {
    }

    public Debt(int id, int vendorId, String debtorName, String debtorPhone, BigDecimal amount, BigDecimal balance, String description, Date debtDate, Date dueDate, Date createdDate, String status) {
        this.id = id;
        this.vendorId = vendorId;
        this.debtorName = debtorName;
        this.debtorPhone = debtorPhone;
        this.amount = amount;
        this.balance = balance;
        this.description = description;
        this.debtDate = debtDate;
        this.dueDate = dueDate;
        this.createdDate = createdDate;
        this.status = status;
    }

    public Debt(int vendorId, String debtorName, BigDecimal amount, Date debtDate) {
        this.vendorId = vendorId;
        this.debtorName = debtorName;
        this.amount = amount;
        this.debtDate = debtDate;
        this.status = "ACTIVE";
    }

    public boolean isOverdue() {
        if (dueDate == null || !status.equals("ACTIVE")) {
            return false;
        }
        return new Date().after(dueDate);
    }

    public boolean isPaid() {
        return status.equals("PAID");
    }

    public BigDecimal getPaidAmount() {
        if (amount == null || balance == null)
            return BigDecimal.ZERO;
        return amount.subtract(balance);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getDebtorPhone() {
        return debtorPhone;
    }

    public void setDebtorPhone(String debtorPhone) {
        this.debtorPhone = debtorPhone;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDebtDate() {
        return debtDate;
    }

    public void setDebtDate(Date debtDate) {
        this.debtDate = debtDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
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
