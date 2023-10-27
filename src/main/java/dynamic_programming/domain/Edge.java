package dynamic_programming.domain;

import java.util.Objects;

/**
 * An edge is located between two nodes
 */

public record Edge (Node n0, Node n1) {

    public static Edge of(Node n0, Node n1) {
        return new Edge(n0, n1);
    }

    public boolean isValid(int xMax, int yMax) {
        return n0.isValid(xMax,yMax) && n1.isValid(xMax,yMax);
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
        return otherCasted.n0.equals(this.n0) && otherCasted.n1.equals(this.n1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.n0().x(),this.n0().y(),this.n1().x(),this.n1().y());
    }


    @Override
    public String toString() {
        return "n0 = "+ n0 +"; n1  = "+ n1;
    }

}
