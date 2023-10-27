package dynamic_programming.domain;

/***
 * State can also be named node
 *
 */

import java.util.Objects;

public record State(int x, int y) {

    public static State newState(int x, int y) {
        return new State(x, y);
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
        if (!(other instanceof State otherCasted)) {
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
