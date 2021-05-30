import java.util.Objects;

public abstract class Employee implements Comparable<Employee> {
    private String surname;
    private String organization;
    private double coefficient;


    public Employee(String surname, String organization, double coefficient) throws BadInputException {
        if (surname == null)
            throw new BadInputException("Surname can't be null");
        if (organization == null)
            throw new BadInputException("Organization can't be null");
        if (coefficient < 0)
            throw new BadInputException("Coefficient of caffeine can't be less than 0");
        this.surname = surname;
        this.organization = organization;
        this.coefficient = coefficient;
    }

    public Employee() throws BadInputException {
        this("", "", 0);
    }

    public abstract double salary();

    @Override
    public int compareTo(Employee o) {
        return Double.compare(salary() / coefficient, o.salary() / o.coefficient);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(surname).append(", organization = ").append(organization).append(", ");
        sb.append(" coefficient = ").append(coefficient).append(" salary = ").append(salary());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return Double.compare(employee.coefficient, coefficient) == 0 &&
                surname.equals(employee.surname) &&
                organization.equals(employee.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(surname, organization, coefficient);
    }

    public String getSurname() {
        return surname;
    }

    public String getOrganization() {
        return organization;
    }

    public double getCoefficient() {
        return coefficient;
    }
}