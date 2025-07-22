package vd.vendordebtors.model.debt;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DebtStatistics {
    private int totalDebts;
    private BigDecimal totalOriginalAmount;
    private BigDecimal totalCurrentAmount;
    private BigDecimal avgDebtAmount;
    private int activeDebts;
    private int paidDebts;
    private int archivedDebts;
    private int overdueDebts;

    public DebtStatistics() {
    }

    public int getTotalDebts() {
        return totalDebts;
    }

    public void setTotalDebts(int totalDebts) {
        this.totalDebts = totalDebts;
    }

    public BigDecimal getTotalOriginalAmount() {
        return totalOriginalAmount;
    }

    public void setTotalOriginalAmount(BigDecimal totalOriginalAmount) {
        this.totalOriginalAmount = totalOriginalAmount;
    }

    public BigDecimal getTotalCurrentAmount() {
        return totalCurrentAmount;
    }

    public void setTotalCurrentAmount(BigDecimal totalCurrentAmount) {
        this.totalCurrentAmount = totalCurrentAmount;
    }

    public BigDecimal getAvgDebtAmount() {
        return avgDebtAmount;
    }

    public void setAvgDebtAmount(BigDecimal avgDebtAmount) {
        this.avgDebtAmount = avgDebtAmount;
    }

    public int getActiveDebts() {
        return activeDebts;
    }

    public void setActiveDebts(int activeDebts) {
        this.activeDebts = activeDebts;
    }

    public int getPaidDebts() {
        return paidDebts;
    }

    public void setPaidDebts(int paidDebts) {
        this.paidDebts = paidDebts;
    }

    public int getArchivedDebts() {
        return archivedDebts;
    }

    public void setArchivedDebts(int archivedDebts) {
        this.archivedDebts = archivedDebts;
    }

    public int getOverdueDebts() {
        return overdueDebts;
    }

    public void setOverdueDebts(int overdueDebts) {
        this.overdueDebts = overdueDebts;
    }

    public BigDecimal getTotalPaidAmount() {
        if (totalOriginalAmount == null || totalCurrentAmount == null) {
            return BigDecimal.ZERO;
        }
        return totalOriginalAmount.subtract(totalCurrentAmount);
    }

    public double getPaymentPercentage() {
        if (totalOriginalAmount == null || totalOriginalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getTotalPaidAmount().divide(totalOriginalAmount, 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)).doubleValue();
    }
}
