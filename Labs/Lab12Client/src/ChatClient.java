import xmltools.ProjectValidator;
import xmltools.SupportFiles;
import xmltools.SupportXML;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    private MessagePrinter messagePrinter;
    private String DTD_SERVER;
    private String SCHEMA_SERVER;
    private String DTD_CLIENT = "ClientRequest.dtd";
    private String SCHEMA_CLIENT = "ClientRequest.xsd";
    private SupportFiles supportFiles;
    private ProjectValidator clientValidator;
    private ClientRequest clientRequest;
    private ServerReply serverReply;
    private SupportXML supportXML;
    private String fileXMLName;


    public ChatClient() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Server IP: ");
        String ip = scanner.nextLine();
        try {
            socket = new Socket(ip, Protocol.PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
            supportFiles = new SupportFiles(in, out);
            DTD_SERVER = supportFiles.receiveFile();
            SCHEMA_SERVER = supportFiles.receiveFile();
            clientValidator = new ProjectValidator(DTD_SERVER, SCHEMA_SERVER);
            supportXML = new SupportXML(clientValidator);
            supportFiles.sendFile(DTD_CLIENT);
            supportFiles.sendFile(SCHEMA_CLIENT);

            System.out.println("Your nick: ");
            String str = scanner.nextLine();
            clientRequest = new ClientRequest(str, Protocol.REGISTRATION);
            fileXMLName = supportXML.makeXMLFile(clientRequest);
            supportFiles.sendFile(fileXMLName);
            serverReply = (ServerReply) supportXML.readFromXMLFile(supportFiles.receiveFile());
            String ans = serverReply.getReply();

            while (!(str.equals("-e") || !ans.equals(Protocol.ERROR))) {
                System.out.println("Select another nick: ");
                str = scanner.nextLine();
                clientRequest.setMessage(str);
                fileXMLName = supportXML.makeXMLFile(clientRequest);
                supportFiles.sendFile(fileXMLName);
                serverReply = (ServerReply) supportXML.readFromXMLFile(supportFiles.receiveFile());
                ans = serverReply.getReply();
            }
            messagePrinter = new MessagePrinter();
            messagePrinter.start();
            clientRequest.setRequest(Protocol.MESSAGE);
            while (!str.equals("-e")) {
                str = scanner.nextLine();
                clientRequest.setMessage(str);
                fileXMLName = supportXML.makeXMLFile(clientRequest);
                supportFiles.sendFile(fileXMLName);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            close();
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
            messagePrinter.setStop();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private class MessagePrinter extends Thread {
        private boolean stopped;
        private String str;

        void setStop() {
            stopped = true;
        }

        @Override
        public void run() {
            try {
                while (!stopped) {
                    serverReply = (ServerReply) supportXML.readFromXMLFile(supportFiles.receiveFile());
                    str = serverReply.getReply();
                    System.out.println(str);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
