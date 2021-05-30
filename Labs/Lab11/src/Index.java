import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

interface IndexBase {
    String[] getKeys(Comparator<String> comp);

    void put(String key, long value);

    boolean contains(String key);

    //Long[] get(String key);
    List<Long> get(String key);
}

class KeyComp implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        // right order:
        return o1.compareTo(o2);
    }
}

class KeyCompReverse implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        // reverse order:
        return o2.compareTo(o1);
    }
}

class IndexOne2One implements Serializable, IndexBase {
    // Unique keys
    // class release version:
    private static final long serialVersionUID = 1L;

    private Map<String, Long> map;

    public IndexOne2One() {
        map = new HashMap<>();
    }

    @Override
    public String[] getKeys(Comparator<String> comp) {
        String[] ans = new String[]{};
        ans = map.keySet().toArray(ans);
        Arrays.sort(ans, comp);
        return ans;
    }

    @Override
    public void put(String key, long value) {
        map.put(key, value);
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public List<Long> get(String key) {
        List<Long> pos = new Vector<>();
        pos.add(map.get(key));
        return pos;
    }
}

class IndexOne2Many implements Serializable, IndexBase {
    // Not unique keys
    // class release version:
    private static final long serialVersionUID = 1L;

    private Map<String, List<Long>> map;

    public IndexOne2Many() {
        map = new TreeMap<>();
    }

    @Override
    public String[] getKeys(Comparator<String> comp) {
        String[] ans = new String[]{};
        ans = map.keySet().toArray(ans);
        Arrays.sort(ans, comp);
        return ans;
    }

    @Override
    public void put(String key, long value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            List<Long> arr = new Vector<>();
            arr.add(value);
            map.put(key, arr);
        }
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public List<Long> get(String key) {
        return map.get(key);
    }
}

public class Index implements Serializable, Closeable {
    // class release version:
    private static final long serialVersionUID = 1L;
    private IndexBase departmentNumbers;
    private IndexBase fullNames;
    private IndexBase employmentDates;
    private transient String filename;

    private Index() {
        departmentNumbers = new IndexOne2Many();
        fullNames = new IndexOne2One();
        employmentDates = new IndexOne2Many();
    }

    public static Index load(String name) throws IOException, ClassNotFoundException {
        Index obj;
        try {
            FileInputStream file = new FileInputStream(name);
            try (ZipInputStream zis = new ZipInputStream(file)) {
                ZipEntry zen = zis.getNextEntry();
                if (!zen.getName().equals(Buffer.zipEntryName)) {
                    throw new IOException("Invalid block format");
                }
                try (ObjectInputStream ois = new ObjectInputStream(zis)) {
                    obj = (Index) ois.readObject();
                }
            }
        } catch (FileNotFoundException e) {
            obj = new Index();
        }
        if (obj != null) {
            obj.save(name);
        }
        return obj;
    }

    public void test(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee is not nullable");
        }
        if (fullNames.contains(employee.getFullName()))
            throw new IllegalArgumentException("Name must be unique");
    }

    public void put(Employee employee, long value) {
        test(employee);
        fullNames.put(employee.getFullName(), value);
        departmentNumbers.put(Integer.toString(employee.getDepartmentNumber()), value);
        employmentDates.put(Employee.getFormatter().format(employee.getEmploymentDate()), value);
    }

    private void save(String name) {
        filename = name;
    }

    private void saveAs(String name) throws IOException {
        FileOutputStream file = new FileOutputStream(name);
        try (ZipOutputStream zos = new ZipOutputStream(file)) {
            zos.putNextEntry(new ZipEntry(Buffer.zipEntryName));
            zos.setLevel(ZipOutputStream.DEFLATED);
            try (ObjectOutputStream oos = new ObjectOutputStream(zos)) {
                oos.writeObject(this);
                zos.closeEntry();
            }
        }
    }

    @Override
    public void close() throws IOException {
        saveAs(filename);
    }

    public IndexBase getDepartmentNumbers() {
        return departmentNumbers;
    }

    public IndexBase getFullNames() {
        return fullNames;
    }

    public IndexBase getEmploymentDates() {
        return employmentDates;
    }
}
