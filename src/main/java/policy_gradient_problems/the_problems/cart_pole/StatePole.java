package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import static common.MyFunctions.sqr2;
import static common.RandUtils.randomNumberBetweenZeroAndOne;

@Builder
public record StatePole(
        double angle,
        double x,
        double angleDot,
        double xDot,
        int nofSteps) {


    public StatePole copy() {
        return StatePole.builder()
                .angle(angle).x(x).angleDot(angleDot).xDot(xDot).nofSteps(nofSteps)
                .build();
    }

    public ArrayRealVector asRealVector() {
        return new ArrayRealVector(new double[]{angle,x,angleDot,xDot});
    }

    public static  StatePole newUprightAndStill() {
        return StatePole.builder()
                .angle(0).x(0).angleDot(0).xDot(0).nofSteps(0)
                .build();
    }
    public static  StatePole newAngleAndPosRandom(ParametersPole p) {
        return StatePole.builder()
                .angle(-p.angleMax()+ randomNumberBetweenZeroAndOne()*2*p.angleMax())
                .x(-p.xMax()+ randomNumberBetweenZeroAndOne()*2*p.xMax())
                .angleDot(0)
                .xDot(0)
                .nofSteps(0)
                .build();
    }


    public StatePole calcNew(int action, ParametersPole parameters) {
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double temp = getTempVariable(action, sinTheta, angleDot,parameters);
        double thetaAcc = getThetaAcc(cosTheta, sinTheta, temp,parameters);
        double xAcc = getXAcc(cosTheta, temp, thetaAcc,parameters);
        double tau=parameters.tau();
        return  StatePole.builder()
                .angle(angle+tau*angleDot)
                .x(x+tau*xDot)
                .angleDot(angleDot+tau*thetaAcc)
                .xDot(xDot+tau*xAcc)
                .nofSteps(nofSteps+1)
                .build();
    }

    private double getTempVariable(int action, double sinTheta, double thetaDot, ParametersPole parameters) {
        double force=(action==0)?-parameters.forceMagnitude():parameters.forceMagnitude();
        return force + parameters.massPoleTimesLength() * sqr2.apply(thetaDot) * sinTheta;
    }

    private double getXAcc(double cosTheta, double temp, double thetaAcc, ParametersPole parameters) {
        return temp - parameters.massPoleTimesLength()* thetaAcc * cosTheta / parameters.massTotal();
    }

    private double getThetaAcc(double cosTheta, double sinTheta, double temp, ParametersPole parameters) {
        double denom = parameters.length() * (4.0 / 3.0 - parameters.massPole() * sqr2.apply(cosTheta) /
                parameters.massTotal());
        return (parameters.g() * sinTheta - cosTheta * temp) / denom;
    }


}
