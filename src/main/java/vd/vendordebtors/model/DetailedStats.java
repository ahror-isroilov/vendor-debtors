package vd.vendordebtors.model;

import java.math.BigDecimal;

public class DetailedStats {
    private int totalDebts;
    private BigDecimal totalAmount;
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
    private int totalPending;
    private int totalPaid;
    private int totalOverdue;

    public DetailedStats() {}

    public DetailedStats(int totalDebts, BigDecimal totalAmount, BigDecimal totalBalance, 
                        BigDecimal averageBalance, int totalPending, int totalPaid, int totalOverdue) {
        this.totalDebts = totalDebts;
        this.totalAmount = totalAmount;
        this.totalBalance = totalBalance;
        this.averageBalance = averageBalance;
        this.totalPending = totalPending;
        this.totalPaid = totalPaid;
        this.totalOverdue = totalOverdue;
    }

    public int getTotalDebts() {
        return totalDebts;
    }

    public void setTotalDebts(int totalDebts) {
        this.totalDebts = totalDebts;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getAverageBalance() {
        return averageBalance;
    }

    public void setAverageBalance(BigDecimal averageBalance) {
        this.averageBalance = averageBalance;
    }

    public int getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(int totalPending) {
        this.totalPending = totalPending;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(int totalPaid) {
        this.totalPaid = totalPaid;
    }

    public int getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(int totalOverdue) {
        this.totalOverdue = totalOverdue;
    }
}