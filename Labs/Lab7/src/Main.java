import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {

    static final String FILENAME = "Employees.dat";
    static final String INDEXNAME = "Employees.idx";
    static final String FILENAMEBAK = "Employees.~dat";
    static final String INDEXNAMEBAK = "Employees.~idx";

    public static void main(String[] args) {
        String option = "";
        Scanner scanner = new Scanner(System.in);
        while (!option.equals("e")) {
            try {
                System.out.println("Command: ");
                option = scanner.nextLine();
                switch (option) {
                    case "a": { //append
                        appendFile(false);
                        break;
                    }
                    case "az": { //append zipped
                        appendFile(true);
                        break;
                    }
                    case "p": { //print
                        printFile();
                        break;
                    }
                    case "ps": { //print sorted
                        printSorted(false);
                        break;
                    }
                    case "prs": { //print reverse sorted
                        printSorted(true);
                        break;
                    }
                    case "pk": { //print by key
                        printByKey();
                        break;
                    }
                    case "pla": { //print large than key
                        printByKey(new KeyCompReverse());
                        break;
                    }
                    case "ple": { //print less than key
                        printByKey(new KeyComp());
                        break;
                    }
                    case "d": { //delete
                        delete();
                        break;
                    }
                    case "e": { //exit
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Invalid command: " + option);
                    }
                }
            } catch (IOException | ClassNotFoundException | IllegalArgumentException | DateTimeParseException e) {
                System.err.println(e);
            }
        }
    }

    private static void appendFile(boolean zipped) throws IOException, ClassNotFoundException {
        Scanner fin = new Scanner(System.in);
        try (Index idx = Index.load(INDEXNAME);
             RandomAccessFile raf = new RandomAccessFile(FILENAME, "rw")) {
            System.out.println("Number of elements: ");
            int num = Integer.parseInt(fin.nextLine());
            for (int i = 0; i < num; i++) {
                Employee employee = Employee.read(fin, System.out);
                if (employee == null)
                    break;
                idx.test(employee);
                long pos = Buffer.writeObject(raf, employee, zipped);
                idx.put(employee, pos);
            }
        }
    }

    private static void printRecord(RandomAccessFile raf, long pos)
            throws ClassNotFoundException, IOException {
        SimpleBooleanProperty wasZipped = new SimpleBooleanProperty(false);
        Employee employee = (Employee) Buffer.readObject(raf, pos, wasZipped);
        if (wasZipped.getValue()) {
            System.out.print(" compressed");
        }
        System.out.println(" record at position " + pos + ": \n" + employee);
    }

    private static void printRecord(RandomAccessFile raf, String key,
                                    IndexBase pidx) throws ClassNotFoundException, IOException {
        List<Long> poss = pidx.get(key);
        for (Long pos : poss) {
            System.out.print("*** Key: " + key + " points to");
            printRecord(raf, pos);
        }
    }

    private static void printFile() throws IOException, ClassNotFoundException {
        try (RandomAccessFile raf = new RandomAccessFile(FILENAME, "rw")) {
            long pos = raf.getFilePointer();
            int rec = 1;
            while (pos < raf.length()) {
                System.out.print("#" + rec);
                rec++;
                printRecord(raf, pos);
                pos = raf.getFilePointer();
            }
        }
    }

    private static void printSorted(boolean reverse)
            throws ClassNotFoundException, IOException {
        try (Index idx = Index.load(INDEXNAME);
             RandomAccessFile raf = new RandomAccessFile(FILENAME, "rw")) {
            IndexBase pidx = indexByName(idx);
            String[] keys = pidx.getKeys(reverse ? new KeyCompReverse() : new KeyComp());
            for (String key : keys) {
                printRecord(raf, key, pidx);
            }
        }
    }

    private static IndexBase indexByName(Index index) {
        Scanner fin = new Scanner(System.in);
        System.out.println("Index name: ");
        String name = fin.nextLine();
        switch (name) {
            case "dn": //department numbers
                return index.getDepartmentNumbers();
            case "fn": //full names
                return index.getFullNames();
            case "ed": //employment dates
                return index.getEmploymentDates();
            default:
                throw new IllegalArgumentException("Invalid index name: " + name);
        }
    }

    private static String getKey() {
        Scanner fin = new Scanner(System.in);
        System.out.println("Key: ");
        return fin.nextLine();
    }

    private static void printByKey()
            throws ClassNotFoundException, IOException {
        try (Index idx = Index.load(INDEXNAME);
             RandomAccessFile raf = new RandomAccessFile(FILENAME, "rw")) {
            IndexBase pidx = indexByName(idx);
            String key = getKey();
            if (!pidx.contains(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            printRecord(raf, key, pidx);
        }
    }

    private static void printByKey(Comparator<String> comp)
            throws ClassNotFoundException, IOException {
        try (Index idx = Index.load(INDEXNAME);
             RandomAccessFile raf = new RandomAccessFile(FILENAME, "rw")) {
            IndexBase pidx = indexByName(idx);
            String key = getKey();
            if (!pidx.contains(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
           String[] keys = pidx.getKeys(comp);
            for (String cur : keys) {
                if (cur.equals(key)) {
                    break;
                }
                printRecord(raf, cur, pidx);
            }
        }
    }

    private static void delete() throws ClassNotFoundException, IOException {
        List<Long> poss;
        try (Index idx = Index.load(INDEXNAME)) {
            IndexBase pidx = indexByName(idx);
            String key = getKey();
            if (!pidx.contains(key)) {
                throw new IllegalArgumentException("Key not found: " + key);
            }
            poss = pidx.get(key);
        }
        File data = new File(FILENAME), index = new File(INDEXNAME), dataBak = new File(FILENAMEBAK), indexBak = new File(INDEXNAMEBAK);
        if ((!dataBak.exists() || dataBak.delete()) && (!indexBak.exists() || indexBak.delete()) && data.renameTo(dataBak) && index.renameTo(indexBak)) {
            try (Index idx = Index.load(INDEXNAME);
                 RandomAccessFile fileBak = new RandomAccessFile(FILENAMEBAK, "rw");
                 RandomAccessFile file = new RandomAccessFile(FILENAME, "rw")) {
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
