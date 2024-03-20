package utils;

import java.util.Objects;

public class Quad {
    public Integer first;
    public Integer second;
    public Integer third;
    public Integer fourth;

    public Quad(Integer first, Integer second, Integer third, Integer fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public Quad(Integer first, Integer second, Pair others) {
        this.first = first;
        this.second = second;
        this.third = others.first;
        this.first = others.second;
    }

    public Quad(Pair others, Integer third, Integer fourth) {
        this.first = others.first;
        this.second = others.second;
        this.third = third;
        this.fourth = fourth;
    }

    public Quad(Pair first, Pair second) {
        this.first = first.first;
        this.second = first.second;
        this.third = second.first;
        this.fourth = second.second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quad quad = (Quad) o;
        return Objects.equals(first, quad.first) && Objects.equals(second,
                quad.second) && Objects.equals(third, quad.third) && Objects.equals(fourth,
                quad.fourth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third, fourth);
    }

    @Override
    public String toString() {
        return "Quad{" +
               "first=" + first +
               ", second=" + second +
               ", third=" + third +
               ", fourth=" + fourth +
               '}';
    }

}
