package vd.vendordebtors.dao;

import vd.vendordebtors.model.Vendor;
import vd.vendordebtors.util.DBConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendorDao {
    public Vendor authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM VENDORS WHERE username = ? AND status = 'ACTIVE'";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (verifyPassword(password, storedPassword)) {
                    return mapResultSetToVendor(rs);
                }
            }
        }
        return null;
    }

    public Vendor getVendorById(int vendorId) throws SQLException {
        String sql = "SELECT * FROM VENDORS WHERE id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToVendor(rs);
            }
        }
        return null;
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM DUAL WHERE EXISTS(SELECT 1 FROM VENDORS  WHERE  username = ?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean existsByPhone(String phone) throws SQLException {
        String sql = "SELECT 1 FROM DUAL WHERE EXISTS(SELECT 1 FROM VENDORS  WHERE  phone = ?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public boolean createVendor(Vendor vendor) throws SQLException {
        String sql = "INSERT INTO VENDORS (username, password, name, phone,status) VALUES (?, ?, ?, ?,?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vendor.getUsername());
            stmt.setString(2, hashPassword(vendor.getPassword()));
            stmt.setString(3, vendor.getName());
            stmt.setString(4, vendor.getPhone());
            stmt.setString(5, "ACTIVE");
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateVendor(Vendor vendor) throws SQLException {
        String sql = "UPDATE VENDORS SET name = ?, phone = ? WHERE vendor_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vendor.getName());
            stmt.setString(3, vendor.getPhone());
            stmt.setInt(5, vendor.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    private Vendor mapResultSetToVendor(ResultSet rs) throws SQLException {
        Vendor vendor = new Vendor();
        vendor.setId(rs.getInt("id"));
        vendor.setUsername(rs.getString("username"));
        vendor.setName(rs.getString("name"));
        vendor.setPhone(rs.getString("phone"));
        vendor.setCreatedDate(rs.getDate("created_date"));
        vendor.setStatus(rs.getString("status"));
        return vendor;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}
