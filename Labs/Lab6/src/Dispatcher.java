import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Dispatcher extends Entity {
    private ConsoleView view;
    private List<Driver> drivers;
    private List<Car> motorPool;
    private List<Ride> rides;

    public Dispatcher(ResourceBundle resources, List<Driver> drivers, List<Car> motorPool) {
        super(Type.DISPATCHER, resources);
        view = new ConsoleView(this, resources);
        this.drivers = drivers;
        this.motorPool = motorPool;
        this.rides = new ArrayList<>();
    }

    public void open(File source) throws IOException, ClassNotFoundException {
        List<Entity> list = Connector.read(source);
        motorPool.clear();
        rides.clear();
        drivers.clear();
        for (Entity object : list) {
            object.resources = resources;
            switch (object.getType()) {
                case CAR: {
                    motorPool.add((Car) object);
                    break;
                }
                case RIDE: {
                    rides.add((Ride) object);
                    break;
                }
                case DRIVER: {
                    drivers.add((Driver) object);
                    break;
                }
            }
        }
    }

    public void save(File dest) throws IOException {
        Connector.write(dest, drivers, motorPool, rides);
    }

    public void showInform() {
        view.show(drivers, motorPool, rides);
    }

    public void addRide(String from, String to, String surname, String name, String number) throws IncorrectParameterException {
        Driver driver = findDriver(surname, name);
        if (driver == null) {
            throw new IncorrectParameterException(resources.getString("noDriver"));
        }
        if (!driver.isWorking()) {
            throw new IncorrectParameterException(resources.getString("driverSuspended"));
        }
        if (driver.isBusy()) {
            throw new IncorrectParameterException(resources.getString("busyDriver"));
        }
        Car car = findCar(number);
        if (car == null) {
            throw new IncorrectParameterException(resources.getString("noCar"));
        }
        if (!car.isServiceable()) {
            throw new IncorrectParameterException(resources.getString("servCar"));
        }
        if (car.isBusy()) {
            throw new IncorrectParameterException(resources.getString("busyCar"));
        }
        driver.setBusy(true);
        car.setBusy(true);
        rides.add(new Ride(resources, from, to, false, driver, car));
    }

    public void suspendDriver(String surname, String name) throws IncorrectParameterException {
        Driver driver = findDriver(surname, name);
        if (driver == null) {
            throw new IncorrectParameterException(resources.getString("noDriver"));
        }
        if (!driver.isWorking()) {
            throw new IncorrectParameterException(resources.getString("alreadySuspended"));
        }
        driver.setWorking(false);
    }

    public void finishRide(String surname, String name, boolean serviceable) throws IncorrectParameterException {
        Driver driver = findDriver(surname, name);
        if (driver == null) {
            throw new IncorrectParameterException(resources.getString("noDriver"));
        }
        if (!driver.isBusy()) {
            throw new IncorrectParameterException(resources.getString("noBusyDriver"));
        }
        Ride ride = findRide(driver);
        if (ride == null) {
            throw new IncorrectParameterException(resources.getString("noRide"));
        }
        ride.setCompleted(true);
        driver.setBusy(false);
        ride.getCar().setServiceable(serviceable);
    }

    public void requestRepair(String surname, String name) throws IncorrectParameterException {
        Driver driver = findDriver(surname, name);
        if (driver == null) {
            throw new IncorrectParameterException(resources.getString("noDriver"));
        }
        Ride ride = findRide(driver);
        if (ride == null) {
            throw new IncorrectParameterException(resources.getString("noRide"));
        }
        ride.getCar().setServiceable(false);
    }

    private Ride findRide(Driver driver) {
        for (Ride ride : rides) {
            if (ride.getDriver().equals(driver)) {
                return ride;
            }
        }
        return null;
    }

    private Driver findDriver(String surname, String name) {
        for (Driver driver : drivers) {
            if (driver.getSurname().equals(surname) && driver.getName().equals(name)) {
                return driver;
            }
        }
        return null;
    }

    private Car findCar(String number) {
        for (Car car : motorPool) {
            if (car.getNumber().equals(number)) {
                return car;
            }
        }
        return null;
    }
}
