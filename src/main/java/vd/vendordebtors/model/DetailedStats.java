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
    private int paymentPercentage;

    public DetailedStats(int totalDebts, BigDecimal totalAmount, BigDecimal totalBalance,
                         BigDecimal averageBalance, int totalPending, int totalPaid, int totalOverdue, int paymentPercentage) {
        this.totalDebts = totalDebts;
        this.totalAmount = totalAmount;
        this.totalBalance = totalBalance;
        this.averageBalance = averageBalance;
        this.totalPending = totalPending;
        this.totalPaid = totalPaid;
        this.totalOverdue = totalOverdue;
        this.paymentPercentage = paymentPercentage;
    }

    public int getTotalDebts() {
        return totalDebts;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public BigDecimal getAverageBalance() {
        return averageBalance;
    }

    public int getTotalPending() {
        return totalPending;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public int getTotalOverdue() {
        return totalOverdue;
    }

    public int getPaymentPercentage() {
        return paymentPercentage;
    }
}