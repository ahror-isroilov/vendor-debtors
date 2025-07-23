package vd.vendordebtors.util;

import vd.vendordebtors.model.debt.Debt;

import java.util.List;

public record PageResult(int page, int size, String statsStartDate, String statsEndDate, List<Debt> debts, int totalDebts, int totalPages, int startEntry, int endEntry) {
}
