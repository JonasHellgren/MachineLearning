package common.math;

import com.google.common.math.IntMath;
import java.util.Optional;

public record Discrete2DPos(
        int x,
        int y
) {

    public static Discrete2DPos of(int x,int y) {
        return new Discrete2DPos(x,y);
    }

    public Discrete2DPos copy() {
        return  Discrete2DPos.of(x,y);
    }

    public Discrete2DPos move(Discrete2DVector vector) {
        return  Discrete2DPos.of(
                x+ vector.dx(),
                y+ vector.dy());
    }

    public Discrete2DPos clip(Discrete2DPos minPos, Discrete2DPos maxPos) {
        return  Discrete2DPos.of(
                MathUtils.clip(x,minPos.x,maxPos.x),
                MathUtils.clip(y,minPos.y,maxPos.y));
    }

    public Optional<Discrete2DPos> midPos(Discrete2DPos other) {
        int sumX = x + other.x;
        int sumY = y + other.y;
        return (IntMath.mod(sumX,2)!=0 || IntMath.mod(sumY,2)!=0 )
                ? Optional.empty()
                : Optional.of(Discrete2DPos.of(sumX/2,sumY/2));

    }

    public double distance(Discrete2DPos other) {
        return distance(this,other);
    }

    public static double distance(Discrete2DPos posA, Discrete2DPos posB) {
        int dx=posB.x-posA.x;
        int dy=posB.y-posA.y;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }


}
