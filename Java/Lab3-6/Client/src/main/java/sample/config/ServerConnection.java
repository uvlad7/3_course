package sample.config;

import sample.dao.DataConnection;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Properties;

public class ServerConnection {
    private static ServerConnection instance;

    public DataConnection dataConnection;

    private ServerConnection(Properties properties) {
        try {
            dataConnection = (DataConnection) Naming.lookup(properties.getProperty("server.url")
                    + properties.getProperty("server.ip") + "/" + properties.getProperty("server.name"));
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {
            ex.printStackTrace();
        }
    }

    public static ServerConnection getInstance(Properties properties) {
        if (instance == null) {
            instance = new ServerConnection(properties);
        }
        return instance;
    }
}
