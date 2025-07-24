package vd.vendordebtors.dao;

import oracle.jdbc.OracleTypes;
import vd.vendordebtors.model.DetailedStats;
import vd.vendordebtors.model.VendorStats;
import vd.vendordebtors.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

public class StatsDao {

    public VendorStats getVendorStats(int vendorId) throws SQLException {
        BigDecimal totalAmount = getTotalDebt(vendorId);
        BigDecimal totalBalance = getTotalBalance(vendorId);
        BigDecimal totalOverdue = getTotalOverdue(vendorId);
        return new VendorStats(totalAmount, totalBalance, totalOverdue);
    }

    private BigDecimal getTotalDebt(int vendorId) throws SQLException {
        String sql = "{ ? = call get_vendor_total_debt(?) }";
        return executeAndReturn(vendorId, sql);
    }

    private BigDecimal getTotalBalance(int vendorId) throws SQLException {
        String sql = "{ ? = call get_vendor_total_balance(?) }";
        return executeAndReturn(vendorId, sql);
    }

    private BigDecimal getTotalOverdue(int vendorId) throws SQLException {
        String sql = "{ ? = call get_overdue_debts_total(?) }";
        return executeAndReturn(vendorId, sql);
    }

    private BigDecimal executeAndReturn(int vendorId, String sql) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.registerOutParameter(1, Types.NUMERIC);
            stmt.setInt(2, vendorId);
            stmt.execute();
            BigDecimal result = stmt.getBigDecimal(1);
            return result != null ? result : BigDecimal.ZERO;
        }
    }

    public DetailedStats getDetailedStatsByDate(int vendorId, Date startDate, Date endDate) throws SQLException {
        String sql = "{ ? = call get_debt_stats_by_date(?, ?, ?) }";

        try (Connection conn = DBConnection.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.setInt(2, vendorId);
            stmt.setDate(3, new java.sql.Date(startDate.getTime()));
            stmt.setDate(4, new java.sql.Date(endDate.getTime()));
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(1)) {
                if (rs.next()) {
                    return new DetailedStats(
                            rs.getInt("total_debts"),
                            rs.getBigDecimal("total_amount"),
                            rs.getBigDecimal("total_balance"),
                            rs.getBigDecimal("average_balance"),
                            rs.getInt("total_pending"),
                            rs.getInt("total_paid"),
                            rs.getInt("total_overdue"),
                            rs.getInt("payment_percentage")
                    );
                } else {
                    return new DetailedStats(0, BigDecimal.ZERO, BigDecimal.ZERO,
                            BigDecimal.ZERO, 0, 0, 0, 0);
                }
            }
        }
    }
}