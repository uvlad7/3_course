public class Record {
    private Employee employee;
    private Boolean compressed;

    public Record(Employee employee, Boolean compressed) {
        this.employee = employee;
        this.compressed = compressed;
    }

    @Override
    public String toString() {
        return employee.toString() + (compressed ? ", compressed" : "");
    }
}