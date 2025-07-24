package vd.vendordebtors.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VendorStats {
    private final BigDecimal totalAmount;
    private final BigDecimal totalBalance;
    private final BigDecimal totalOverdue;
    private final int paymentPercentage;

    public VendorStats(BigDecimal totalAmount, BigDecimal totalBalance, BigDecimal totalOverdue) {
        this.totalAmount = totalAmount;
        this.totalBalance = totalBalance;
        this.totalOverdue = totalOverdue;
        this.paymentPercentage = calculatePaymentPercentage(this.totalAmount, this.totalBalance);
    }

    private int calculatePaymentPercentage(BigDecimal totalAmount, BigDecimal totalBalance) {
        int calculationScale = 4;
        RoundingMode roundingMode = RoundingMode.HALF_UP;
        if (totalBalance.compareTo(BigDecimal.ZERO) == 0) return 100;
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
        BigDecimal ratio = totalBalance.divide(totalAmount, calculationScale, roundingMode);
        BigDecimal oneMinusRatio = BigDecimal.ONE.subtract(ratio);
        BigDecimal percentageBigDecimal = oneMinusRatio.multiply(new BigDecimal("100"));

        return percentageBigDecimal.setScale(0, roundingMode).intValue();
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public BigDecimal getTotalOverdue() {
        return totalOverdue;
    }

    public int getPaymentPercentage() {
        return paymentPercentage;
    }
}