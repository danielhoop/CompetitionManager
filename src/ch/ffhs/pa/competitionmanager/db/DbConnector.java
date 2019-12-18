package ch.ffhs.pa.competitionmanager.db;

import ch.danielhoop.utils.ExceptionVisualizer;

import java.sql.*;

/**
 * Class to create database connection and handle exceptions.
 */
public class DbConnector {

    private String address;
    private String user;
    private String password;

    /**
     * Initialize the DbConnector. No connection will be opened!
     * @param address The address (connection string) of the database.
     * @param user The database user.
     * @param password The password for the database user.
     */
    public DbConnector(String address, String user, String password) {
        this.address = address;
        this.user = user;
        this.password = password;
    }

    /**
     * Get the database connection. Attention: Don't forget to close the connection if you are done, using the method DbConnector.closeConnection(Connection connection)
     * @return A database connection.
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(address, user, password);
            return conn;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    ExceptionVisualizer.showAndAddMessage(ex, "DbConnector.getConnection() -> conn.close(): ");
                }
            }
            e.printStackTrace();
            ExceptionVisualizer.showAndAddMessage(e, "DbConnector.getConnection() -> DriverManager.getConnection(): ");
        }
        return null;
    }

    /**
     * Create a statement of an open connection. Catch errors and show them visually and in stout.
     * @param connection The database connection
     * @return A statement
     */
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

    /**
     * Close an open connection. Catch errors and show them visually and in stout.
     * @param connection The database connection.
     */
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

    /**
     * Close an open statement. Catch errors and show them visually and in stout.
     * @param statement The statement.
     */
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
