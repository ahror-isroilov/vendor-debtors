package vd.vendordebtors.dao;

import oracle.jdbc.OracleTypes;
import vd.vendordebtors.model.VendorStats;
import vd.vendordebtors.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;

public class StatsDao {

    public VendorStats getVendorStats(int vendorId) throws SQLException {
        BigDecimal totalAmount = getTotalDebt(vendorId);
        BigDecimal totalBalance = getTotalBalance(vendorId);
        BigDecimal totalOverdue = getTotalOverdue(vendorId);
        
        return new VendorStats(totalAmount, totalBalance, totalOverdue);
    }

    private BigDecimal getTotalDebt(int vendorId) throws SQLException {
        String sql = "{ ? = call get_vendor_total_debt(?) }";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.registerOutParameter(1, Types.NUMERIC);
            stmt.setInt(2, vendorId);
            stmt.execute();
            
            BigDecimal result = stmt.getBigDecimal(1);
            return result != null ? result : BigDecimal.ZERO;
        }
    }

    private BigDecimal getTotalBalance(int vendorId) throws SQLException {
        String sql = "SELECT NVL(SUM(balance), 0) FROM DEBTS WHERE vendor_id = ? AND status <> 'PAID'";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getTotalOverdue(int vendorId) throws SQLException {
        String sql = "{ call get_overdue_debts_report(?, ?) }";
        
        try (Connection conn = DBConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, vendorId);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            
            BigDecimal totalOverdue = BigDecimal.ZERO;
            
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                while (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("current_balance");
                    if (balance != null) {
                        totalOverdue = totalOverdue.add(balance);
                    }
                }
            }
            
            return totalOverdue;
        }
    }
}