package sample;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import sample.dao.DataConnection;
import sample.dao.impl.MySQLDataConnection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Properties properties = new Properties();
        properties.load(new InputStreamReader(Main.class.getResourceAsStream("/resource.properties")));
        Reflections reflections = new Reflections("sample.dao", new SubTypesScanner(false));
        Class<? extends DataConnection> connectionClass = reflections.getSubTypesOf(DataConnection.class).stream().filter(c ->
                c.getSimpleName().equals(properties.getProperty("connector"))).findAny().orElse(MySQLDataConnection.class);
        final DataConnection server = connectionClass.getConstructor(Properties.class).newInstance(properties);
        final Registry registry = LocateRegistry.createRegistry(Integer.parseInt(properties.getProperty("port")));
        registry.rebind(properties.getProperty("server.name"), server);
        System.out.println("server.name=" + properties.getProperty("server.name"));
        System.out.println("server.ip=" + InetAddress.getLocalHost().getHostAddress() + ":" + properties.getProperty("port"));
        System.out.println("server.url=rmi://");
    }
}
