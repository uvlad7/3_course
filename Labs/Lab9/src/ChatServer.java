import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
                Connection connection = new Connection(socket);
                connection.start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
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

    private class Connection extends Thread {
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;
        private String name;

        Connection(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                close();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    name = in.readLine();
                    if (!connections.containsKey(name) || name.equals("-e")) {
                        break;
                    }
                    out.println(Protocol.ERROR);
                }
                out.println(Protocol.CONNECTED);
                if (!name.equals("-e")) {
                    synchronized (connections) {
                        connections.put(name, this);
                        for (Connection connection : connections.values()) {
                            connection.out.println(name + " joined the chat");
                        }
                    }
                    String str;
                    while (true) {
                        str = in.readLine();
                        if (str.equals("-e"))
                            break;
                        synchronized (connections) {
                            for (Connection connection : connections.values()) {
                                connection.out.println(name + ": " + str);
                            }
                        }
                    }
                    synchronized (connections) {
                        for (Connection connection : connections.values()) {
                            connection.out.println(name + " leave the chat");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                close();
            }
        }

        void close() {
            try {
                in.close();
                out.close();
                socket.close();
                synchronized (connections) {
                    connections.remove(name);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            } finally {
                synchronized (connections) {
                    if (connections.isEmpty()) {
                        closeAll();
                        System.exit(0);
                    }
                }
            }
        }
    }
}
