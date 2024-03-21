package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

import static common.MyFunctions.sqr2;
import static common.RandUtils.getRandomDouble;

@Builder
public record VariablesPole(
                            double angle,
                            double x,
                            double angleDot,
                            double xDot,
                            int nofSteps) {



    public VariablesPole copy() {
        return VariablesPole.builder()
                .angle(angle).x(x).angleDot(angleDot).xDot(xDot).nofSteps(nofSteps)
                .build() ;
    }

    public RealVector asRealVector() {
        return new ArrayRealVector(new double[]{angle, x, angleDot, xDot});
    }

    public List<Double> asList() {
        return List.of(angle, x, angleDot, xDot);
    }

    public static VariablesPole newUprightAndStill() {
        return VariablesPole.builder()
                .angle(0).x(0).angleDot(0).xDot(0).nofSteps(0)
                .build();
    }

    public static VariablesPole newAngleAndPosRandom(ParametersPole p) {
        return VariablesPole.builder()
                .angle(getRandomDouble(-p.angleMax(),p.angleMax()))
                .x(getRandomDouble(-p.xMax(),p.xMax()))
                .angleDot(0)
                .xDot(0)
                .nofSteps(0)
                .build();
    }

    public static VariablesPole newAllRandom(ParametersPole p) {
        return VariablesPole.builder()
                .angle(getRandomDouble(-p.angleMax(),p.angleMax()))
                .x(getRandomDouble(-p.xMax(),p.xMax()))
                .angleDot(getRandomDouble(-1,1))
                .xDot(getRandomDouble(-1,1))
                .nofSteps(0)
                .build();
    }




    @Override
    public String toString() {
        return asList().toString();
    }

}
