package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;

import static common.MyFunctions.sqr2;

@Builder
public record CartPoleState(
        double angle,
        double x,
        double angleDot,
        double xDot,
        int nofSteps) {

    public  CartPoleState calcNew(int action, CartPoleParameters parameters) {
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double temp = getTempVariable(action, sinTheta, angleDot,parameters);
        double thetaAcc = getThetaAcc(cosTheta, sinTheta, temp,parameters);
        double xAcc = getXAcc(cosTheta, temp, thetaAcc,parameters);
        double tau=parameters.tau();
        return  CartPoleState.builder()
                .angle(angle+tau*angleDot)
                .x(x+tau*xDot)
                .angleDot(angleDot+tau*thetaAcc)
                .xDot(xDot+tau*xAcc)
                .nofSteps(nofSteps+1)
                .build();
    }

    private double getTempVariable(int action, double sinTheta, double thetaDot,CartPoleParameters parameters) {
        double force=(action==0)?-parameters.forceMagnitude():parameters.forceMagnitude();
        return force + parameters.massPoleTimesLength() * sqr2.apply(thetaDot) * sinTheta;
    }

    private double getXAcc(double cosTheta, double temp, double thetaAcc,CartPoleParameters parameters) {
        return temp - parameters.massPoleTimesLength()* thetaAcc * cosTheta / parameters.massTotal();
    }

    private double getThetaAcc(double cosTheta, double sinTheta, double temp, CartPoleParameters parameters) {
        double denom = parameters.length() * (4.0 / 3.0 - parameters.massPole() * sqr2.apply(cosTheta) /
                parameters.massTotal());
        return (parameters.g() * sinTheta - cosTheta * temp) / denom;
    }


}
