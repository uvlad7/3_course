package sample.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SqlConnector {
    private static SqlConnector instance;

    private Connection connection;

    private SqlConnector(Properties properties) {
        try {
            connection = DriverManager.getConnection(properties.getProperty("db.url"),
                    properties.getProperty("db.user"), properties.getProperty("db.password"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static SqlConnector getInstance(Properties properties) {
        if (instance == null) {
            instance = new SqlConnector(properties);
        }
        return instance;
    }

}
