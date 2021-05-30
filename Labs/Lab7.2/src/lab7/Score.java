package lab7;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Score implements Serializable {
    public static final String TypeOfWorkDel = ",";
    public static final String areaDel = "\n";
    static final String P_Firm = "Firm";
    static final String P_TypeOfWork = "TypeOfWork";
    static final String P_Unit = "Unit";
    static final String P_PriceOfUnit = "PriceOfUnit";
    static final String P_Date = "Date";
    static final String P_VolumeOfUnits = "VolumeOfUnits";
    // class release version:
    private static final long serialVersionUID = 1L;
    private static GregorianCalendar curCalendar = new GregorianCalendar();
    // areas with prompts:
    String Firm;
    String TypeOfWork;
    String Unit;
    double PriceOfUnit;
    String Date;
    String VolumeOfUnits;

    public Score() {
    }

    // validation methods:
    static Boolean validDate(String str) {
        try {
            if (str == null) return false;
            if (!Character.isDigit(str.charAt(0)) || !Character.isDigit(str.charAt(1)) || !Character.isDigit(str.charAt(2)) || !Character.isDigit(str.charAt(3)) || str.charAt(4) != '.' || !Character.isDigit(str.charAt(5)) || !Character.isDigit(str.charAt(6)) || str.charAt(7) != '.' || !Character.isDigit(str.charAt(8)) || !Character.isDigit(str.charAt(9)))
                return false;
            StringTokenizer arg = new StringTokenizer(str, " .");
            int year = Integer.parseInt(arg.nextToken());
            if (year < 0 || year > (new GregorianCalendar().get(Calendar.YEAR))) return false;
            String month = arg.nextToken();
            if (month.compareTo("12") > 0) return false;
            String day = arg.nextToken();
            if (day.compareTo("31") > 0) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    static Boolean validStr(String str) {
        if (str.isEmpty())
            return false;
        return true;
    }

    static Boolean validYear(int year) {
        return year > 0 && year <= curCalendar.get(Calendar.YEAR);
    }

    public static Boolean nextRead(Scanner fin, PrintStream out) {
        return nextRead(P_Firm, fin, out);
    }

    static Boolean nextRead(final String prompt, Scanner fin, PrintStream out) {
        out.print(prompt);
        out.print(": ");
        return fin.hasNextLine();
    }

    public static Score read(Scanner fin, PrintStream out)
            throws IOException {
        String str;
        String err = "Invalid arguments!";
        Score score = new Score();

        score.Firm = fin.nextLine();

        if (!nextRead(P_TypeOfWork, fin, out)) return null;
        score.TypeOfWork = fin.nextLine();

        if (!nextRead(P_Unit, fin, out)) return null;
        score.Unit = fin.nextLine();

        if (!nextRead(P_PriceOfUnit, fin, out)) return null;
        str = fin.nextLine();
        score.PriceOfUnit = Double.parseDouble(str);

        if (!nextRead(P_Date, fin, out)) return null;
        score.Date = fin.nextLine();
        if (!validDate(score.Date)) throw new IOException(err);

        if (!nextRead(P_VolumeOfUnits, fin, out)) return null;
        score.VolumeOfUnits = fin.nextLine();

        return score;
    }

    public String toString() {
        return //new String (
                Firm + areaDel +
                        TypeOfWork + areaDel +
                        Unit + areaDel +
                        PriceOfUnit + areaDel +
                        Date + areaDel +
                        VolumeOfUnits
                //)
                ;
    }
}
