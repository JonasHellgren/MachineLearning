package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;

import static common.MyFunctions.defaultIfNullDouble;
import static common.MyFunctions.defaultIfNullInteger;


public record ParametersPole(
        double g,
        double pi,
        double massCart,
        double massPole,
        double massTotal,
        double length,
        double massPoleTimesLength,
        double forceMagnitude,
        double tau,
        double angleMax,
        double angleDotMax,
        double xMax,
        double xDotMax,
        double maxNofSteps,
        double rewardNonFail,
        double rewardFail) {

    static final double MASS_POLE = 0.1, MASS_CART = 1;
    static final double LENGTH = 0.5;
    static final double PI = 3.141592;
    public static final double ANGLE_MAX = 12 * 2 * PI / 360, X_MAX = 2;
    public static final double G = 9.81, TAU = 0.02;
    public static final double FORCE_MAGNITUDE = 10;
    public static final int MAX_NOF_STEPS = 100;
    public static final double ANGLE_DOT_MAX = 0.3;
    public static final double X_DOT_MAX = 10;

    @Builder
    public ParametersPole(Double massCart,
                          Double massPole,
                          Double massTotal,
                          Double length,
                          Double massPoleTimesLength,
                          Double forceMagnitude,
                          Double tau,
                          Double angleMax,
                          Double angleDotMax,
                          Double xMax,
                          Double xDotMax,
                          Integer maxNofSteps,
                          Double rewardNonFail,
                          Double rewardFail) {
        this(   G,
                PI,
                defaultIfNullDouble.apply(massCart, MASS_CART),
                defaultIfNullDouble.apply(massPole, MASS_POLE),
                defaultIfNullDouble.apply(massTotal, MASS_CART + MASS_POLE),
                defaultIfNullDouble.apply(length, LENGTH),
                defaultIfNullDouble.apply(massPoleTimesLength, MASS_POLE * LENGTH),
                defaultIfNullDouble.apply(forceMagnitude, FORCE_MAGNITUDE),
                defaultIfNullDouble.apply(tau, TAU),
                defaultIfNullDouble.apply(angleMax, ANGLE_MAX),
                defaultIfNullDouble.apply(angleDotMax, ANGLE_DOT_MAX),
                defaultIfNullDouble.apply(xMax, X_MAX),
                defaultIfNullDouble.apply(xDotMax, X_DOT_MAX),
                defaultIfNullInteger.apply(maxNofSteps, MAX_NOF_STEPS),
                defaultIfNullDouble.apply(rewardNonFail, 1d),
                defaultIfNullDouble.apply(rewardFail, 0d));
    }


    public static ParametersPole newDefault() {
        return ParametersPole.builder().build();
    }

    public static ParametersPole newWithMaxNofSteps(int maxNofSteps) {
        return ParametersPole.builder().maxNofSteps(maxNofSteps).build();
    }

    public static ParametersPole newRewardNegativeFail() {
        return ParametersPole.builder().rewardFail(-1d).rewardNonFail(0d).build();
    }

}
