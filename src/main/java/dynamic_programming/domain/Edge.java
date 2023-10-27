package dynamic_programming.domain;

import java.util.Objects;

/**
 * An edge is located between two nodes
 */

public record Edge (State s0, State s1) {

    public static Edge newEdge(State s0, State s1) {
        throwIfSameStates(s0, s1);
        return new Edge(s0, s1);
    }

    private static void throwIfSameStates(State s0, State s1) {
        if (s0.equals(s1)) {
            throw new IllegalArgumentException("An edge cant connect the same node");
        }
    }

    public boolean isValid(int xMax, int yMax) {
        return s0.isValid(xMax,yMax) && s1.isValid(xMax,yMax);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Edge otherCasted)) {
            return false;
        }
        return otherCasted.s0.equals(this.s0) && otherCasted.s1.equals(this.s1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.s0().x(),this.s0().y(),this.s1().x(),this.s1().y());
    }


    @Override
    public String toString() {
        return "s0 = "+s0+"; s1  = "+s1;
    }

}
