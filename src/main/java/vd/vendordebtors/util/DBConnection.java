package vd.vendordebtors.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USERNAME = "vendor";
    private static final String PASSWORD = "psw123";

    private static final HikariDataSource hds;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setDriverClassName("oracle.jdbc.driver.OracleDriver");

        config.setMaximumPoolSize(10);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        hds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return hds.getConnection();
    }

    public static void closePool() {
        hds.close();
    }
}
