import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static int PORT = 5050;
    private final List<Connection> connections;
    private Socket socket;
    private ServerSocket serverSocket;


    public Server() {
        connections = new ArrayList<>();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started");
            System.out.println("Server IP:\r\n" + InetAddress.getLocalHost().getHostAddress());
            while (true) {
                System.out.println("Waiting for connection...");
                socket = serverSocket.accept();
                Connection connection = new Connection(socket, this);
                connections.add(connection);
                connection.start();
            }
        } catch (IOException ignored) {
            stop();
        }
    }

    public void remove(Connection connection) {
        synchronized (connections) {
            connections.remove(connection);
        }
        if (connections.isEmpty()) {
            stop();
        }
    }

    private void stop() {
        try {
            System.out.println("Server stopped");
            serverSocket.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
