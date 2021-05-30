import java.util.Objects;
import java.util.ResourceBundle;

public class Driver extends Entity {
    private String surname;
    private String name;
    private boolean working;
    private boolean busy;

    public Driver(ResourceBundle resources, String surname, String name, boolean working, boolean busy) {
        super(Type.DRIVER, resources);
        this.surname = surname;
        this.name = name;
        this.working = working;
        this.busy = busy;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Driver)) return false;
        Driver driver = (Driver) o;
        return surname.equals(driver.surname) &&
                name.equals(driver.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surname, name);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder(resources.getString("driver"));
        sb.append("{").append(resources.getString("created")).append(": ").append(getCreationDate());
        sb.append(", ").append(resources.getString("surname")).append(": ").append(surname);
        sb.append(", ").append(resources.getString("name")).append(": ").append(name);
        sb.append(", ").append(resources.getString("working")).append(": ");
        sb.append(resources.getString(Boolean.toString(working)));
        sb.append(", ").append(resources.getString("busy")).append(": ");
        sb.append(resources.getString(Boolean.toString(busy))).append('}');
        return sb.toString();
    }
}
