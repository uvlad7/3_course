import java.util.Objects;
import java.util.ResourceBundle;

public class Car extends Entity {
    private String number;
    private boolean serviceable;
    private boolean busy;

    public Car(ResourceBundle resources, String number, boolean serviceable, boolean busy) {
        super(Type.CAR, resources);
        this.number = number;
        this.serviceable = serviceable;
        this.busy = busy;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isServiceable() {
        return serviceable;
    }

    public void setServiceable(boolean serviceable) {
        this.serviceable = serviceable;
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
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return number.equals(car.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(resources.getString("car"));
        sb.append("{").append(resources.getString("created")).append(": ").append(getCreationDate());
        sb.append(", ").append(resources.getString("number")).append(": ").append(number);
        sb.append(", ").append(resources.getString("serviceable")).append(": ");
        sb.append(resources.getString(Boolean.toString(serviceable)));
        sb.append(", ").append(resources.getString("busy")).append(": ");
        sb.append(resources.getString(Boolean.toString(busy))).append('}');
        return sb.toString();
    }
}
