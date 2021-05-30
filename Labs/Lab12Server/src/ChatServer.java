import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


public class ChatServer {

    private final Map<String, Connection> connections;
    private ServerSocket server;

    public ChatServer() {
        connections = new HashMap<>();
        try {
            server = new ServerSocket(Protocol.PORT);
            System.out.println("Server IP:\r\n" + InetAddress.getLocalHost().getHostAddress());
            while (true) {
                Socket socket = server.accept();
                Connection connection = new Connection(socket, this);
                connection.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            closeAll();
        }
    }

    public void closeAll() {
        try {
            server.close();
            synchronized (connections) {
                for (Connection connection : connections.values()) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public Map<String, Connection> getConnections() {
        return connections;
    }
}
