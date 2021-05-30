/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter writer = new PrintWriter(System.out)) {
            List<String> lines = new ArrayList<>();
            String str;
            while ((str = input.readLine()) != null && str.length() != 0) {
                lines.add(str);
            }
            List<Pair> list = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                String[] words = lines.get(i).split("\\s");
                for (String word : words) {
                    if (isFloat(word)) {
                        try {
                            float number = Float.parseFloat(word);
                            list.add(new Pair(i + 1, number, word));
                        } catch (NumberFormatException ignored) {

                        }
                    }
                }
            }
            list.stream().sorted().forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isFloat(String s) {
        if (s.length() == 0)
            return false;
        return (s.charAt(s.length() - 1) == 'F' || s.charAt(s.length() - 1) == 'f');
    }

    private static class Pair implements Comparable<Pair> {
        private int line;
        private float number;
        private String literal;

        public Pair(int line, float number, String literal) {
            this.line = line;
            this.number = number;
            this.literal = literal;
        }

        @Override
        public String toString() {
            return line + ": " + literal;
        }

        @Override
        public int compareTo(Pair o) {
            return Float.compare(Math.abs(number), Math.abs(o.number));
        }
    }
}
*/

import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Begining of the work(press ctrl+z for ending of the programm):");
        Scanner in = new Scanner(System.in);
        ArrayList<String> words = new ArrayList<>();
        while (in.hasNextLine()) {
            StringTokenizer wordsInLine = new StringTokenizer(in.nextLine());
            while (wordsInLine.hasMoreTokens())
                words.add(wordsInLine.nextToken());
        }
        String longestWord = "";
        int maxLong = 0;
        for (String word : words) {
            if (word.length() > maxLong) {
                maxLong = word.length();
                longestWord = word;
            }
        }
        int count = 0;
        for (String word : words) {
            if (word.equals(longestWord))
                count++;
            System.out.println(longestWord + " " + count);
        }
    }
}