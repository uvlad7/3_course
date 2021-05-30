import java.util.StringTokenizer;

public class Tokenizer {
    public static String tokenize(String string) throws InvalidBracketSequenceException {
        StringTokenizer stringTokenizer = new StringTokenizer(string, "()", true);
        int col = 0;
        StringBuilder stringBuilder = new StringBuilder();
        String cur, prev = "";
        boolean add = true;
        while (stringTokenizer.hasMoreTokens()) {
            cur = stringTokenizer.nextToken();
            if (cur.equals("(")) {
                if (!add)
                    stringBuilder.append(prev);
                col++;
                add = false;
            } else if (cur.equals(")")) {
                if (col == 0)
                    throw new InvalidBracketSequenceException("Closing bracket without opening");
                col--;
                add = true;
            } else {
                prev = cur;
                if (add)
                    stringBuilder.append(cur);
            }
        }
        if (col != 0)
            throw new InvalidBracketSequenceException("Opening bracket without closing");
        return stringBuilder.toString();
    }
}