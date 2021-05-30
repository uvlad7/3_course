package app.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class SqlConnector {
    private static SqlConnector instance;

    private Connection connection;

    private SqlConnector(Map<String, String> properties) {
        try {
            connection = DriverManager.getConnection(properties.get("db.url"),
                    properties.get("db.user"), properties.get("db.password"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public static SqlConnector getInstance(Map<String, String> properties) {
        if (instance == null) {
            instance = new SqlConnector(properties);
        }
        return instance;
    }

}
