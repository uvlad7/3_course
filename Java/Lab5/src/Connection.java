import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;


public class Connection extends Thread {

    private Socket socket;
    private Action action = new Action();
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Server server;

    public Connection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void interrupt() {
        try {
            System.out.println("Client unconnected");
            socket.close();
            in.close();
            out.close();
        } catch (IOException ignored) {

        } finally {
            server.remove(this);
        }
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connected");
            do {
                try {
                    action = (Action) in.readObject();
                    switch (action.getType()) {
                        case PUT: {
                            try {
                                File dest = new File(action.getFilename().substring(0, action.getFilename().length() - 4) + socket.getInetAddress().toString().replaceAll("[/\\\\]", "") + ".txt");
                                Files.write(Paths.get(dest.toURI()), Encryptor.decrypt(action.getMessage()).getBytes());
                                out.writeObject(new Action("", Type.PUT, ""));
                            } catch (Exception e) {
                                out.writeObject(new Action("", Type.ERROR, ""));
                            }
                            out.flush();
                            break;
                        }
                        case GET: {
                            try {
                                File src = new File(action.getFilename().substring(0, action.getFilename().length() - 4) + socket.getInetAddress().toString().replaceAll("[/\\\\]", "") + ".txt");
                                String text = new String(Files.readAllBytes(Paths.get(src.toURI())));
                                out.writeObject(new Action(Encryptor.encrypt(text), Type.GET, ""));
                            } catch (Exception e) {
                                out.writeObject(new Action("", Type.ERROR, ""));
                            }
                            out.flush();
                            break;
                        }
                    }
                } catch (Exception ignored) {
                }
            } while (action.getType() != Type.STOP);
        } catch (Exception ignored) {
        } finally {
            interrupt();
        }
    }
}
