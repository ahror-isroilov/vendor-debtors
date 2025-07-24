package vd.vendordebtors.dao;

import oracle.jdbc.OracleTypes;
import vd.vendordebtors.model.debt.DebtTransaction;
import vd.vendordebtors.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DebtTransactionDao {

    public List<DebtTransaction> getDebtTransactions(int debtId) throws SQLException {
        String sql = "{call get_debt_transactions(?, ?)}";

        try (Connection conn = DBConnection.getConnection(); CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, debtId);
            stmt.registerOutParameter(2, OracleTypes.CURSOR);
            stmt.execute();
            try (ResultSet rs = (ResultSet) stmt.getObject(2)) {
                return mapRsToDebtTransactions(rs);
            }
        }
    }

    private List<DebtTransaction> mapRsToDebtTransactions(ResultSet rs) throws SQLException {
        List<DebtTransaction> transactions = new ArrayList<>();
        while (rs.next()) {
            DebtTransaction transaction = new DebtTransaction();
            transaction.setId(rs.getInt("transaction_id"));
            transaction.setTransactionType(rs.getString("transaction_type"));
            transaction.setAmount(rs.getBigDecimal("transaction_amount"));
            transaction.setDescription(rs.getString("transaction_description"));
            transaction.setCreatedDate(rs.getDate("transaction_date"));
            transaction.setStatus(rs.getString("transaction_status"));
            transactions.add(transaction);
        }

        return transactions;
    }

    public boolean createTransaction(DebtTransaction transaction) throws SQLException {
        String sql = "INSERT INTO DEBT_TRANSACTION (debt_id, transaction_type, amount, description, created_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            try {
                stmt.setInt(1, transaction.getDebtId());
                stmt.setString(2, transaction.getTransactionType());
                stmt.setBigDecimal(3, transaction.getAmount());
                stmt.setString(4, transaction.getDescription());
                stmt.setTimestamp(5, new Timestamp(transaction.getCreatedDate().getTime()));
                stmt.setString(6, "SUCCESS");
                return stmt.executeUpdate() > 0;
            } catch (Exception e) {
                return false;
            }
        }
    }
    
    public boolean deleteTransaction(int transactionId) throws SQLException {
        String sql = "DELETE FROM DEBT_TRANSACTION WHERE ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteAllTransactionsForDebt(int debtId) throws SQLException {
        String sql = "DELETE FROM DEBT_TRANSACTION WHERE debt_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, debtId);
            stmt.executeUpdate();
            return true;
        }
    }
}