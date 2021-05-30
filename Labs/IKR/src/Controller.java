import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements ControllerInterface {
    final private Map<String, Map.Entry<Double, Double>> salaryMap;
    private List<Employee> model;
    private ViewInterface view;
    private double minRev;
    private double maxRev;

    public Controller() {
        view = new View(this);
        model = new ArrayList<>();
        salaryMap = new HashMap<>();
        maxRev = Integer.MIN_VALUE;
        minRev = Integer.MAX_VALUE;
    }

    @Override
    public boolean open(String path) {
        try {
            model = Reader.read(path);
            salaryMap.clear();
            for (Employee employee : model) {
                if (salaryMap.containsKey(employee.getOrganization())) {
                    Map.Entry<Double, Double> item = salaryMap.get(employee.getOrganization());
                    salaryMap.replace(employee.getOrganization(), Map.entry(Math.min(employee.salary(), item.getKey()), Math.max(employee.salary(), item.getValue())));
                } else {
                    salaryMap.put(employee.getOrganization(), Map.entry(employee.salary(), employee.salary()));
                }
            }
            if (model.get(0) instanceof Seller) {
                for (Employee employee : model) {
                    Seller seller = (Seller) employee;
                    minRev = Math.min(minRev, seller.getRevenue());
                    maxRev = Math.max(maxRev, seller.getRevenue());
                }
            }
            view.showInfo("Number of employees", model.size());
            return true;
        } catch (IOException e) {
            view.showError(e);
            return false;
        }
    }

    @Override
    public void showValuable(String org) {
        List<Employee> sorted = new ArrayList<>(model);
        Collections.copy(sorted, model);
        sorted = sorted.stream().filter(employee -> employee.getOrganization().equalsIgnoreCase(org)).collect(Collectors.toList());
        Collections.sort(sorted, new BySalaryComparator());
        view.showValuable(sorted);
    }

    @Override
    public void showLazy() {
        List<Employee> sorted = new ArrayList<>(model);
        Collections.copy(sorted, model);
        Collections.sort(sorted);
        view.showLazy(sorted);
    }

    @Override
    public void showNames() {
        Set<String> names = new HashSet<>();
        for (Employee elem : model) {
            names.add(elem.getOrganization());
        }
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        view.showNames(sorted);
    }

    @Override
    public void search() {
        if (maxRev < 0) {
            view.showInfo("Average seller", "No data");
            return;
        }
        for (Employee employee : model) {
            Seller seller = (Seller) employee;
            if (seller.getRevenue() == (maxRev + minRev) / 2) {
                view.showInfo("Average seller", seller);
                return;
            }
        }
        view.showInfo("Average seller", "Not founded");
    }

    @Override
    public void count(String org) {
        if (salaryMap.containsKey(org)) {
            Map.Entry<Double, Double> item = salaryMap.get(org);
            view.showInfo(org, item.getKey() + " - " + item.getValue());
        } else {
            view.showInfo(org, "No data");
        }
    }
}