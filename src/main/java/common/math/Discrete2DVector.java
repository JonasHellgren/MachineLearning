package common.math;

public record Discrete2DVector(
        int dx,
        int dy
) {

    public static Discrete2DVector of(int dx, int dy) {
        return new Discrete2DVector(dx,dy);
    }

    public boolean equals(Discrete2DVector other) {
        return dx==other.dx && dy==other.dy;
    }


}
