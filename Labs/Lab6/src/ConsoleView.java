import java.io.*;
import java.util.List;
import java.util.ResourceBundle;

public class ConsoleView {
    private String[] menu;
    private BufferedReader reader;
    private Dispatcher dispatcher;
    private int context;
    private boolean run;
    private Thread commandsThread;
    private ResourceBundle resources;

    public ConsoleView(Dispatcher dispatcher, ResourceBundle resources) {
        menu = new String[]{resources.getString("mainMenu"), resources.getString("fileMenu"),
                resources.getString("dispatcherMenu"), resources.getString("driverMenu")};
        this.dispatcher = dispatcher;
        this.resources = resources;
        reader = new BufferedReader(new InputStreamReader(System.in));
        context = 0;
        run = true;
        commandsThread = new Thread(this::dispatch);
        commandsThread.start();
    }

    private void dispatch() {
        while (run) {
            System.out.println(menu[context]);
            try {
                process(Integer.parseInt(reader.readLine()));
            } catch (NumberFormatException | IOException e) {
                System.err.println(resources.getString("invComm"));
            }

        }
    }

    private void process(int command) {
        switch (context) {
            case 0: {
                mainMenu(command);
                break;
            }
            case 1: {
                fileMenu(command);
                break;
            }
            case 2: {
                dispatcherMenu(command);
                break;
            }
            case 3: {
                driverMenu(command);
                break;
            }
        }
    }

    private void mainMenu(int command) {
        if (command < 0 || command > 3)
            throw new NumberFormatException();
        if (command == 0)
            run = false;
        else context = command;
    }

    private void fileMenu(int command) {
        switch (command) {
            case 0: {
                context = 0;
                break;
            }
            case 1: {
                open();
                break;
            }
            case 2: {
                save();
                break;
            }
            case 3: {
                dispatcher.showInform();
                break;
            }
            default: {
                throw new NumberFormatException();
            }
        }
    }

    private void open() {
        try {
            dispatcher.open(new File(getPath()));
        } catch (FileNotFoundException e) {
            System.err.println(resources.getString("fnf"));
        } catch (IOException e) {
            System.err.println(resources.getString("tryAgain"));
        } catch (ClassNotFoundException e) {
            System.err.println(resources.getString("invFile"));
        }
    }

    private void save() {
        try {
            dispatcher.save(new File(getPath()));
        } catch (FileNotFoundException e) {
            System.err.println(resources.getString("fnf"));
        } catch (IOException e) {
            System.err.println(resources.getString("tryAgain"));
        }
    }

    private void dispatcherMenu(int command) {
        switch (command) {
            case 0: {
                context = 0;
                break;
            }
            case 1: {
                addRide();
                break;
            }
            case 2: {
                suspendDriver();
                break;
            }
            default: {
                throw new NumberFormatException();
            }
        }
    }

    private void suspendDriver() {
        try {
            dispatcher.suspendDriver(getSurname(), getName());
        } catch (IncorrectParameterException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(resources.getString("tryAgain"));
        }
    }

    private void addRide() {
        try {
            dispatcher.addRide(getFrom(), getTo(), getSurname(), getName(), getNumber());
        } catch (IncorrectParameterException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(resources.getString("tryAgain"));
        }
    }

    private void driverMenu(int command) {
        switch (command) {
            case 0: {
                context = 0;
                break;
            }
            case 1: {
                finishRide();
                break;
            }
            case 2: {
                requestRepair();
                break;
            }
            default: {
                throw new NumberFormatException();
            }
        }
    }

    private void requestRepair() {
        try {
            dispatcher.requestRepair(getSurname(), getName());
        } catch (IncorrectParameterException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(resources.getString("tryAgain"));
        }
    }

    private void finishRide() {
        try {
            dispatcher.finishRide(getSurname(), getName(), getServiceable());
        } catch (IncorrectParameterException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(resources.getString("tryAgain"));
        } catch (NumberFormatException e) {
            System.err.println(resources.getString("invComm"));
        }
    }

    public void show(List<Driver> drivers, List<Car> motorPool, List<Ride> rides) {
        drivers.forEach(System.out::println);
        motorPool.forEach(System.out::println);
        rides.forEach(System.out::println);
    }

    private String getPath() throws IOException {
        System.out.println(resources.getString("enter") + resources.getString("filename"));
        return reader.readLine();
    }

    private String getSurname() throws IOException {
        System.out.println(resources.getString("enter") + resources.getString("surname"));
        return reader.readLine();
    }

    private String getName() throws IOException {
        System.out.println(resources.getString("enter") + resources.getString("name"));
        return reader.readLine();
    }

    private String getFrom() throws IOException {
        System.out.println(resources.getString("enter") + resources.getString("from"));
        return reader.readLine();
    }

    private String getTo() throws IOException {
        System.out.println(resources.getString("enter") + resources.getString("to"));
        return reader.readLine();
    }

    private String getNumber() throws IOException {
        System.out.println(resources.getString("enter") + resources.getString("number"));
        return reader.readLine();
    }

    private boolean getServiceable() throws IOException, NumberFormatException {
        System.out.println(resources.getString("serviceableMenu"));
        String ans = reader.readLine();
        if (ans.equals("1")) {
            return true;
        } else if (ans.equals("0")) {
            return false;
        } else {
            throw new NumberFormatException();
        }
    }
}
