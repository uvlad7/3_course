import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Employee implements Serializable {
    // class release version:
    private static final long serialVersionUID = 1L;
    private static final int MAX_DEPARTMENT = 100;
    private static final int MIN_DEPARTMENT = 1;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int personnelNumber;
    private int departmentNumber;
    private String fullName;
    private double salary;
    private LocalDate employmentDate;
    private double allowances;
    private double incomeTax;

    public Employee(int personnelNumber, int departmentNumber, String fullName, double salary,
                    String employmentDate, double allowances, double incomeTax) {
        setPersonnelNumber(personnelNumber);
        setDepartmentNumber(departmentNumber);
        setFullName(fullName);
        setSalary(salary);
        setEmploymentDate(LocalDate.parse(employmentDate, formatter));;
    }

    /*public static Employee read(Scanner in, PrintStream out) {
        if (out != null)
            out.println("Employee:");
        Employee employee = new Employee();
        if (requestElement(in, out, "Personnel number: "))
            return null;
        employee.setPersonnelNumber(Integer.parseInt(in.nextLine()));
        if (requestElement(in, out, "Department number: "))
            return null;
        employee.setDepartmentNumber(Integer.parseInt(in.nextLine()));
        if (requestElement(in, out, "Full name: "))
            return null;
        employee.setFullName(in.nextLine());
        if (requestElement(in, out, "Salary: "))
            return null;
        employee.setSalary(Double.parseDouble(in.nextLine()));
        if (requestElement(in, out, "Date of employment: "))
            return null;
        employee.setEmploymentDate(LocalDate.parse(in.nextLine(), formatter));
        if (requestElement(in, out, "Allowances, %: "))
            return null;
        employee.setAllowances(Double.parseDouble(in.nextLine()));
        if (requestElement(in, out, "Income tax: "))
            return null;
        employee.setIncomeTax(Double.parseDouble(in.nextLine()));
        return employee;
    }*/

    public int getPersonnelNumber() {
        return personnelNumber;
    }

    public void setPersonnelNumber(int personnelNumber) {
        this.personnelNumber = personnelNumber;
    }

    public int getDepartmentNumber() {
        return departmentNumber;
    }

    public void setDepartmentNumber(int departmentNumber) {
        if (departmentNumber < MIN_DEPARTMENT || departmentNumber > MAX_DEPARTMENT)
            throw new IllegalArgumentException("Department number must be between " + MIN_DEPARTMENT + " and " + MAX_DEPARTMENT);
        this.departmentNumber = departmentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null)
            throw new IllegalArgumentException("Full name is not nullable");
        this.fullName = fullName;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if (salary <= 0)
            throw new IllegalArgumentException("Salary must be greater than zero");
        this.salary = salary;
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(LocalDate employmentDate) {
        if (fullName == null)
            throw new IllegalArgumentException("The date is not nullable");
        if (employmentDate.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("The date may not be later than the current");
        this.employmentDate = employmentDate;
    }

    public double getAllowances() {
        return allowances;
    }

    public void setAllowances(double allowances) {
        if (allowances < 0)
            throw new IllegalArgumentException("Allowances must not be less than zero");
        if (allowances > 100)
            throw new IllegalArgumentException("Allowances must not be greater than 100%");
        this.allowances = allowances;
    }

    public double getIncomeTax() {
        return incomeTax;
    }

    public void setIncomeTax(double incomeTax) {
        if (incomeTax < 0)
            throw new IllegalArgumentException("Income tax must not be less than zero");
        if (incomeTax >= salary * (100 + allowances) / 100)
            throw new IllegalArgumentException("Income tax must not be greater than payouts");
        this.incomeTax = incomeTax;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee employee = (Employee) o;
        return personnelNumber == employee.personnelNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(personnelNumber);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Personnel number: ").append(personnelNumber);
        sb.append(", Department number: ").append(departmentNumber);
        sb.append(", Full name: ").append(fullName);
        sb.append(", Salary: ").append(salary);
        sb.append(", Date of employment: ").append(formatter.format(employmentDate));
        sb.append(", Allowances: ").append(allowances).append("%");
        sb.append(", Income tax: ").append(incomeTax);
        return sb.toString();
    }

    public static DateTimeFormatter getFormatter() {
        return formatter;
    }
}
