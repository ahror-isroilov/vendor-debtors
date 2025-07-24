package vd.vendordebtors.model;

import vd.vendordebtors.model.debt.Debt;

import java.util.List;
import java.util.Objects;

public final class ExportData {
    private final DetailedStats stats;
    private final List<Debt> debts;

    public ExportData(DetailedStats stats, List<Debt> debts) {
        this.stats = stats;
        this.debts = debts;
    }

    public DetailedStats stats() {
        return stats;
    }

    public List<Debt> debts() {
        return debts;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ExportData) obj;
        return Objects.equals(this.stats, that.stats) &&
                Objects.equals(this.debts, that.debts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stats, debts);
    }

    @Override
    public String toString() {
        return "ExportData[" +
                "stats=" + stats + ", " +
                "debts=" + debts + ']';
    }

}