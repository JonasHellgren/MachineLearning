package dynamic_programming.domain;

/***
 * Corresponds to a state, for speed control of a vehicle y can be speed and x position
 *
 */

import java.util.Objects;

public record Node(int x, int y) {

    public static Node of(int x, int y) {
        return new Node(x, y);
    }

    public boolean isValid(int xMax, int yMax) {
        return x <= xMax && y <= yMax;
    }

    public boolean isXBelowMax(int xMax) {
        return x < xMax;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Node otherCasted)) {
            return false;
        }
        return otherCasted.x == this.x && otherCasted.y == this.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return "(x,y) = (" + x + "," + y + ")";
    }

}
