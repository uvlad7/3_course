import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            try {
                System.out.println(Tokenizer.tokenize(scanner.nextLine()));
            } catch (InvalidBracketSequenceException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
