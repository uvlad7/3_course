import xmltools.ProjectValidator;
import xmltools.SupportFiles;
import xmltools.SupportXML;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection extends Thread {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private String name;
    private ChatServer server;
    private String DTD_CLIENT;
    private String SCHEMA_CLIENT;
    private String DTD_SERVER = "ServerReply.dtd";
    private String SCHEMA_SERVER = "ServerReply.xsd";
    private ServerReply serverReply;
    private SupportFiles supportFiles;
    private ProjectValidator serverValidator;
    private SupportXML supportXML;
    private ClientRequest clientRequest;

    Connection(Socket socket, ChatServer server) {
        this.server = server;
        this.socket = socket;
        serverReply = new ServerReply();
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            supportFiles = new SupportFiles(in, out);
            supportFiles.sendFile(DTD_SERVER);
            supportFiles.sendFile(SCHEMA_SERVER);
            DTD_CLIENT = supportFiles.receiveFile();
            SCHEMA_CLIENT = supportFiles.receiveFile();
            serverValidator = new ProjectValidator(DTD_CLIENT, SCHEMA_CLIENT);
            supportXML = new SupportXML(serverValidator);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            close();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                clientRequest = (ClientRequest) supportXML.readFromXMLFile(supportFiles.receiveFile());
                name = clientRequest.getMessage();
                if (!server.getConnections().containsKey(name) || name.equals("-e")) {
                    break;
                }
                serverReply.setReply(Protocol.ERROR);
                supportFiles.sendFile(supportXML.makeXMLFile(serverReply));
            }
            if (!name.equals("-e")) {
                serverReply.setReply(Protocol.CONNECTED);
                supportFiles.sendFile(supportXML.makeXMLFile(serverReply));
                synchronized (server.getConnections()) {
                    server.getConnections().put(name, this);
                    for (Connection connection : server.getConnections().values()) {
                        serverReply.setReply(name + " joined the chat");
                        connection.supportFiles.sendFile(supportXML.makeXMLFile(serverReply));
                    }
                }
                String str;
                while (true) {
                    clientRequest = (ClientRequest) supportXML.readFromXMLFile(supportFiles.receiveFile());
                    str = clientRequest.getMessage();
                    if (str.equals("-e"))
                        break;
                    synchronized (server.getConnections()) {
                        for (Connection connection : server.getConnections().values()) {
                            serverReply.setReply(name + ": " + str);
                            connection.supportFiles.sendFile(supportXML.makeXMLFile(serverReply));
                        }
                    }
                }
                synchronized (server.getConnections()) {
                    for (Connection connection : server.getConnections().values()) {
                        if (connection != this) {
                            serverReply.setReply(name + " leave the chat");
                            connection.supportFiles.sendFile(supportXML.makeXMLFile(serverReply));
                        }
                    }
                }
            }
        } catch (Exception e) {
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
            synchronized (server.getConnections()) {
                server.getConnections().remove(name);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            synchronized (server.getConnections()) {
                if (server.getConnections().isEmpty()) {
                    server.closeAll();
                    System.exit(0);
                }
            }
        }
    }
}