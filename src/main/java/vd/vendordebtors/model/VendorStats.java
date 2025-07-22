package vd.vendordebtors.model;

import java.math.BigDecimal;

public class VendorStats {
    private BigDecimal totalAmount;
    private BigDecimal totalBalance;
    private BigDecimal totalOverdue;

    public VendorStats() {}

    public VendorStats(BigDecimal totalAmount, BigDecimal totalBalance, BigDecimal totalOverdue) {
        this.totalAmount = totalAmount;
        this.totalBalance = totalBalance;
        this.totalOverdue = totalOverdue;
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

    public BigDecimal getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(BigDecimal totalOverdue) {
        this.totalOverdue = totalOverdue;
    }
}