package dynamic_programming.domain;

import lombok.ToString;

import java.util.Objects;

public record State(int x, int y) {

    public static State newState(int x, int y) {
        return new State(x,y);
    }


    public boolean isValid(int xMax, int yMax) {
        return x<=xMax && y<=yMax;
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
        return "(x,y) = ("+x+","+y + ")";
    }

}
