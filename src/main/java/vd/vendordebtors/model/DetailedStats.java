package vd.vendordebtors.model;

import java.math.BigDecimal;

public record DetailedStats(int totalDebts, BigDecimal totalAmount, BigDecimal totalBalance, BigDecimal averageBalance,
                            int totalPending, int totalPaid, int totalOverdue, int paymentPercentage) {
}