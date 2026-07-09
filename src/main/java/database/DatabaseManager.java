package database;

import util.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

    private DatabaseManager() {
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.USER, DBConfig.PASSWORD);
    }
}
