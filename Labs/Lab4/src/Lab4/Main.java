package Lab4;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        CharSet[] charSets = new CharSet[]{new CharSet(new Character[]{'a', 'b', 'c', 'd'}),
                new CharSet(new Character[]{'c', 'd', 'e', 'f'}),
                new CharSet(new Character[]{'g', 'h', 'i', 'j'}),
                new CharSet(new Character[]{'c', 'i', 'j', 'k', 'l'}),
                new CharSet(new Character[]{'k', 'l', 'm', 'a'})};
        System.out.println("Sets:");
        System.out.println(Arrays.toString(charSets));
        System.out.println("xor:");
        for (int i = 0; i < charSets.length; i++) {
            System.out.println(Main.exclusiveOr(charSets[i], charSets[charSets.length - i - 1]));
        }
        System.out.println("s0&s1: " + CharSet.intersection(charSets[0], charSets[1]));
        System.out.println("s2|s3: " + CharSet.composition(charSets[2], charSets[3]));
        System.out.println("s4\\s3: " + CharSet.difference(charSets[4], charSets[3]));
        charSets[0].and(charSets[1]);
        charSets[2].or(charSets[3]);
        charSets[4].not(charSets[3]);
        System.out.println("s0&s1: " + charSets[0]);
        System.out.println("s2&s3: " + charSets[2]);
        System.out.println("s4\\s3: " + charSets[4]);
        charSets[0].setSet(new Character[]{'a', 'b', 'c', 'd'});
        System.out.println("expect true: " + charSets[0].contains('c'));
        System.out.println("expect 2: " + charSets[0].indexOf('c'));
        System.out.println("expect false: " + charSets[0].contains('e'));
        System.out.println("expect -1: " + charSets[0].indexOf('e'));
        CharSet charSet = new CharSet(charSets[0]);
        System.out.println("expect false: " + (charSets[0] == charSet));
        System.out.println("expect true: " + charSets[0].equals(charSet));
    }

    private static CharSet exclusiveOr(CharSet first, CharSet second) {
        CharSet res = CharSet.composition(first, second);
        res.not(CharSet.intersection(first, second));
        return res;
    }
}
