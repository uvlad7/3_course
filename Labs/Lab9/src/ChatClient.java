import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private MessagePrinter messagePrinter;

    public ChatClient() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Server IP: ");
        String ip = scanner.nextLine();
        try {
            socket = new Socket(ip, Protocol.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Your nick: ");
            String str = scanner.nextLine();
            out.println(str);
            String ans = in.readLine();
            while (!(str.equals("-e") || ans.equals(Protocol.CONNECTED))) {
                System.out.println("Select another nick: ");
                str = scanner.nextLine();
                out.println(str);
                ans = in.readLine();
            }
            messagePrinter = new MessagePrinter();
            messagePrinter.start();
            while (!str.equals("-e")) {
                str = scanner.nextLine();
                out.println(str);
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

        void setStop() {
            stopped = true;
        }

        @Override
        public void run() {
            try {
                while (!stopped) {
                    String str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

}
