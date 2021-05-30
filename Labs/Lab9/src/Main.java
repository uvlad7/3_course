public class Main {
    public static void main(String[] args) {
        switch (args[0]) {
            case "-h": {
                System.out.println("-s: start server\r\n-c: start client\r\n-h: help");
                break;
            }
            case "-s": {
                new ChatServer();
                break;
            }
            case "-c": {
                new ChatClient();
                break;
            }
            default: {
                System.out.println("Incorrect argument. Type '-h' for help");
            }
        }
    }
}
