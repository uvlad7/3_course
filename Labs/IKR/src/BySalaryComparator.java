import java.util.Comparator;

public class BySalaryComparator implements Comparator<Employee> {
    public int compare(Employee first, Employee second) {
        return Double.compare(second.salary(), first.salary());
    }
}
