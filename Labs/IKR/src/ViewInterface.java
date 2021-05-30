import java.util.List;

public interface ViewInterface {
    void showValuable(List<Employee> unsorted);

    void showLazy(List<Employee> sorted);

    void showNames(List<String> names);

    void showInfo(String title, Object message);

    void showError(Exception e);
}
