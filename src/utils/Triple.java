package utils;

import java.util.Objects;

public class Triple {
    public int first;
    public int second;
    public double third;

    public Triple(int first, int second, double third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple triple = (Triple) o;
        return first == triple.first && second == triple.second && Double.compare(third, triple.third) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }

    @Override
    public String toString() {
        return "Triple{" +
               "first=" + first +
               ", second=" + second +
               ", third=" + third +
               '}';
    }
}
