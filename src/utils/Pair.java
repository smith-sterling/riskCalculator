package utils;

import java.util.Objects;

public class Pair implements Comparable<Pair> {
    public Integer first;
    public Integer second;

    public Pair() { }

    public Pair(Integer first, Integer second) {
        this.first = first;
        this.second = second;
    }

    public static Pair p(int f, int s) { return new Pair(f, s); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
               "first=" + first +
               ", second=" + second +
               '}';
    }

    @Override
    public int compareTo(Pair other) {
        return this.first.compareTo(other.first) != 0
                ? -this.first.compareTo(other.first)
                : -this.second.compareTo(other.second);
    }
}
