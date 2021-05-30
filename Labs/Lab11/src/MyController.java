import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyController {
    private String filename;
    private String indexmane;
    private String filenameBak;
    private String indexnameBak;
    private MyFrame frame;

    public MyController() {
        filename = "Employees.dat";
        indexmane = "Employees.idx";
        filenameBak = "Employees.~dat";
        indexnameBak = "Employees.~idx";
        frame = new MyFrame(this);
    }

    void open(String file) {
        filename = file + ".dat";
        indexmane = file + ".idx";
        filenameBak = file + ".~dat";
        indexnameBak = file + ".~idx";
        frame.update(filename);
    }

    void appendFile(boolean zipped, String personnelNumber, String departmentNumber,
                    String fullName, String salary, String employmentDate,
                    String allowances, String incomeTax) throws IOException, ClassNotFoundException {
        try (Index idx = Index.load(indexmane);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            Employee employee = new Employee(Integer.parseInt(personnelNumber), Integer.parseInt(departmentNumber),
                    fullName, Double.parseDouble(salary), employmentDate, Double.parseDouble(allowances), Double.parseDouble(incomeTax));
            idx.test(employee);
            long pos = Buffer.writeObject(raf, employee, zipped);
            idx.put(employee, pos);
        }
    }

    private Record printRecord(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException {
        SimpleBooleanProperty wasZipped = new SimpleBooleanProperty(false);
        Employee employee = (Employee) Buffer.readObject(raf, pos, wasZipped);
        return new Record(employee, wasZipped.getValue());
    }

    private List<Record> printRecord(RandomAccessFile raf, String key,
                             IndexBase pidx) throws ClassNotFoundException, IOException {
        List<Long> poss = pidx.get(key);
        List<Record> records = new ArrayList<>();
        for (Long pos : poss) {
            records.add(printRecord(raf, pos));
        }
        return records;
    }

    void printFile() throws IOException, ClassNotFoundException {
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            long pos = raf.getFilePointer();
            List<Record> records = new ArrayList<>();
            while (pos < raf.length()) {
                records.add(printRecord(raf, pos));
                pos = raf.getFilePointer();
            }
            frame.showData(records);
        }
    }

    void printSorted(boolean reverse, int index)
            throws ClassNotFoundException, IOException {
        try (Index idx = Index.load(indexmane);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            IndexBase pidx = indexByName(idx, index);
            String[] keys = pidx.getKeys(reverse ? new KeyCompReverse() : new KeyComp());
            List<Record> records = new ArrayList<>();
            for (String key : keys) {
                records.addAll(printRecord(raf, key, pidx));
            }
            frame.showData(records);
        }
    }

    private IndexBase indexByName(Index index, int number) {
        switch (number) {
            case 0: //department numbers
                return index.getDepartmentNumbers();
            case 1: //full names
                return index.getFullNames();
            case 2: //employment dates
                return index.getEmploymentDates();
            default:
                throw new IllegalArgumentException("Invalid index: " + number);
        }
    }

    void printByKey(int number, String key)
            throws ClassNotFoundException, IOException {
        try (Index idx = Index.load(indexmane);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            IndexBase pidx = indexByName(idx, number);
            if (!pidx.contains(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            frame.showData(printRecord(raf, key, pidx));
        }
    }

    void printByKey(Comparator<String> comp, int number, String key)
            throws ClassNotFoundException, IOException {
        try (Index idx = Index.load(indexmane);
             RandomAccessFile raf = new RandomAccessFile(filename, "rw")) {
            IndexBase pidx = indexByName(idx, number);
            if (!pidx.contains(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            String[] keys = pidx.getKeys(comp);
            List<Record> records = new ArrayList<>();
            for (String cur : keys) {
                if (cur.equals(key)) {
                    break;
                }
                records.addAll(printRecord(raf, cur, pidx));
            }
            frame.showData(records);
        }
    }

    void delete(int number, String key) throws ClassNotFoundException, IOException {
        List<Long> poss;
        try (Index idx = Index.load(indexmane)) {
            IndexBase pidx = indexByName(idx, number);
            if (!pidx.contains(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            poss = pidx.get(key);
        }
        File data = new File(filename), index = new File(indexmane), dataBak = new File(filenameBak), indexBak = new File(indexnameBak);
        if ((!dataBak.exists() || dataBak.delete()) && (!indexBak.exists() || indexBak.delete()) && data.renameTo(dataBak) && index.renameTo(indexBak)) {
            try (Index idx = Index.load(indexmane);
                 RandomAccessFile fileBak = new RandomAccessFile(filenameBak, "rw");
                 RandomAccessFile file = new RandomAccessFile(filename, "rw")) {
                SimpleBooleanProperty wasZipped = new SimpleBooleanProperty(false);
                Long pos = fileBak.getFilePointer();
                while (pos < fileBak.length()) {
                    Employee employee = (Employee)
                            Buffer.readObject(fileBak, pos, wasZipped);
                    if (!poss.contains(pos)) { // if not found in deleted
                        long ptr = Buffer.writeObject(file, employee, wasZipped.getValue());
                        idx.put(employee, ptr);
                    }
                    pos = fileBak.getFilePointer();
                }
            }
        }
    }
}
