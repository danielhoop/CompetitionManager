package ch.ffhs.pa.competitionmanager.db;

import ch.danielhoop.utils.ExceptionVisualizer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to create database connection.
 */
public class DbConnector {

    private String address;
    private String user;
    private String password;

    public DbConnector(String address, String user, String password) {
        this.address = address;
        this.user = user;
        this.password = password;
    }

    /**
     * Get the database connection.
     * @return A database connection.
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(address, user, password);
            conn.setAutoCommit(false);
            return conn;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    ExceptionVisualizer.show(ex);
                }
            }
            e.printStackTrace();
            ExceptionVisualizer.show(e);
        }
        return null;
    }

    public Statement createStatmentForConnection(Connection connection) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.show(e);
        }
        return stmt;
    }

    public void closeConnection(Connection connection) {
        if (connection == null)
            return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.show(e);
        }
    }
    public void closeStatement(Statement statement) {
        if (statement == null)
            return;
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            ExceptionVisualizer.show(e);
        }
    }
}
