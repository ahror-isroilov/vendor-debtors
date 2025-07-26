package vd.vendordebtors.dao;

import oracle.jdbc.OracleTypes;
import vd.vendordebtors.model.debt.Debt;
import vd.vendordebtors.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebtDao {
    private final DebtTransactionDao debtTransactionDao = new DebtTransactionDao();

    public List<Debt> getDebtsWithPagination(int vendorId, int page, int size) throws SQLException {
        int offset = (page - 1) * size;
        String sql = """
                SELECT * FROM (
                  SELECT d.*, ROW_NUMBER() OVER (ORDER BY d.created_date DESC) as rn 
                  FROM DEBTS d WHERE d.vendor_id = ?
                ) WHERE rn > ? AND rn <= ?""";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendorId);
            stmt.setInt(2, offset);
            stmt.setInt(3, offset + size);
            ResultSet rs = stmt.executeQuery();
            return mapRsToDebt(rs);
        }
    }

    public int getTotalDebtsCount(int vendorId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DEBTS d WHERE d.vendor_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public boolean createDebt(Debt debt) throws SQLException {
        String sql = "INSERT INTO DEBTS (vendor_id, debtor_phone, debtor_name, amount, balance, description, debt_date, due_date, created_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, debt.getVendorId());
            stmt.setString(2, debt.getDebtorPhone());
            stmt.setString(3, debt.getDebtorName());
            stmt.setBigDecimal(4, debt.getAmount());
            stmt.setBigDecimal(5, debt.getBalance());
            stmt.setString(6, debt.getDescription());
            stmt.setDate(7, new java.sql.Date(debt.getDebtDate().getTime()));
            stmt.setDate(8, debt.getDueDate() != null ? new java.sql.Date(debt.getDueDate().getTime()) : null);
            stmt.setDate(9, new java.sql.Date(debt.getCreatedDate().getTime()));
            stmt.setString(10, debt.getStatus());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateDebt(Debt debt) throws SQLException {
        String sql = "UPDATE DEBTS SET debtor_name = ?, debtor_phone = ?, description = ?, debt_date = ?, due_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, debt.getDebtorName());
            stmt.setString(2, debt.getDebtorPhone());
            stmt.setString(3, debt.getDescription());
            stmt.setDate(4, new java.sql.Date(debt.getDebtDate().getTime()));
            stmt.setDate(5, debt.getDueDate() != null ? new java.sql.Date(debt.getDueDate().getTime()) : null);
            stmt.setInt(6, debt.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteDebt(int debtId, int vendorId) throws SQLException {
        try {
            debtTransactionDao.deleteAllTransactionsForDebt(debtId);

            String sql = "DELETE FROM DEBTS d WHERE d.id = ? AND d.vendor_id = ?";
            try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, debtId);
                stmt.setInt(2, vendorId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public List<Debt> searchDebts(String debtorPhone, String debtorName, int vendorId, String status) throws SQLException {
        String sql = "{call search_debts(?, ?, ?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, debtorPhone);
            stmt.setString(2, debtorName);
            stmt.setInt(3, vendorId);
            stmt.setString(4, status);
            stmt.registerOutParameter(5, OracleTypes.CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(5)) {
                return mapRsToDebt(rs);
            }
        }
    }

    private List<Debt> mapRsToDebt(ResultSet rs) throws SQLException {
        List<Debt> debts = new ArrayList<>();
        while (rs.next()) {
            Debt debt = new Debt(
                    rs.getInt("id"),
                    rs.getInt("vendor_id"),
                    rs.getString("debtor_name"),
                    rs.getString("debtor_phone"),
                    rs.getBigDecimal("amount"),
                    rs.getBigDecimal("balance"),
                    rs.getString("description"),
                    rs.getDate("debt_date"),
                    rs.getDate("due_date"),
                    rs.getDate("created_date"),
                    rs.getString("status")
            );
            debts.add(debt);
        }
        return debts;
    }
}
