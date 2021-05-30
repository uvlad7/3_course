import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {

    public static void main(String[] args) {
        ResourceBundle resources;
        if (args.length < 2) {
            resources = ResourceBundle.getBundle("Msg", Locale.getDefault());
        } else {
            resources = ResourceBundle.getBundle("Msg", new Locale(args[0], args[1]));
        }
       /* List<Driver> drivers = new ArrayList<>(Arrays.asList(
                new Driver(resources, resources.getString("surname1"), resources.getString("name1"), true, false),
                new Driver(resources, resources.getString("surname2"), resources.getString("name2"), true, false),
                new Driver(resources, resources.getString("surname3"), resources.getString("name3"), true, false)));
        List<Car> motorPool = new ArrayList<>(Arrays.asList(
                new Car(resources, resources.getString("num1"), true, false),
                new Car(resources, resources.getString("num2"), true, false),
                new Car(resources, resources.getString("num3"), true, false)));
        Dispatcher dispatcher = new Dispatcher(resources, drivers, motorPool);*/
        Dispatcher dispatcher = new Dispatcher(resources, new ArrayList<>(), new ArrayList<>());
        try {
            dispatcher.open(new File("saved.bin"));
        } catch (IOException | ClassNotFoundException ignored) {
        }
    }
}
