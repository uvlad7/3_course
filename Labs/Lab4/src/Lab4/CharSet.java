package Lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CharSet {
    private List<Character> set;

    public CharSet() {
        this(0);
    }

    public CharSet(int length) {
        set = new ArrayList<>(length);
    }

    public CharSet(Character[] set) {
        if (set == null) {
            throw new IllegalArgumentException("Set must be not null");
        }
        this.set = new ArrayList<>(Arrays.asList(set));
    }

    public CharSet(List<Character> set) {
        if (set == null) {
            throw new IllegalArgumentException("Set must be not null");
        }
        this.set = new ArrayList<>(set);
    }

    public CharSet(CharSet other) {
        this(other.set);
    }

    public static CharSet composition(CharSet first, CharSet second) {
        List<Character> temp = new ArrayList<>(first.set);
        for (Character elem : second.set) {
            if (!first.contains(elem)) {
                temp.add(elem);
            }
        }
        return new CharSet(temp);
    }

    public static CharSet intersection(CharSet first, CharSet second) {
        List<Character> temp = new ArrayList<>();
        for (Character elem : second.set) {
            if (first.contains(elem)) {
                temp.add(elem);
            }
        }
        return new CharSet(temp);
    }

    public static CharSet difference(CharSet first, CharSet second) {
        List<Character> temp = new ArrayList<>(first.set);
        for (Character elem : second.set) {
            temp.remove(elem);
        }
        return new CharSet(temp);
    }

    public boolean contains(char elem) {
        return set.contains(elem);
    }

    public void and(CharSet second) {
        List<Character> temp = new ArrayList<>();
        for (Character elem : set) {
            if (second.contains(elem)) {
                temp.add(elem);
            }
        }
        set = temp;
    }

    public void not(CharSet second) {
        for (Character elem : second.set) {
            if (contains(elem)) {
                set.remove(elem);
            }
        }
    }

    public void or(CharSet second) {
        for (Character elem : second.set) {
            if (!contains(elem)) {
                set.add(elem);
            }
        }
    }

    public int indexOf(Character elem) {
        return set.indexOf(elem);
    }

    public void setSet(List<Character> set) {
        this.set = set;
    }

    public void setSet(Character[] set) {
        this.set = new ArrayList<>(Arrays.asList(set));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharSet)) return false;
        CharSet charSet = (CharSet) o;
        return set.equals(charSet.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < set.size(); i++) {
            sb.append(set.get(i)).append(i < set.size() - 1 ? ',' : "");
        }
        sb.append("}");
        return sb.toString();
    }
}
