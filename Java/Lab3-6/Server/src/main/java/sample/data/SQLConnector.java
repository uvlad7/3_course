package sample.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnector {
    private static SQLConnector instance;

    private Connection connection;

    private SQLConnector(Properties properties) {
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

    public static SQLConnector getInstance(Properties properties) {
        if (instance == null) {
            instance = new SQLConnector(properties);
        }
        return instance;
    }

}
