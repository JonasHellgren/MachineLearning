package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;

@Builder
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
        double rewardFail)   {

    static final double MASS_POLE = 0.1, MASS_CART = 1;
    static final double LENGTH = 0.5;
    static final double PI = 3.141592;
    public static final double ANGLE_MAX = 12 * 2 * PI / 360, X_MAX = 2;
    public static final double G = 9.81, TAU = 0.02;
    public static final double FORCE_MAGNITUDE = 10;
    public static final int MAX_NOF_STEPS = 100;
    public static final double ANGLE_DOT_MAX = 0.3;
    public static final int X_DOT_MAX = 10;

    public static ParametersPole newDefault() {
        return ParametersPole.builder()
                .g(G).pi(PI)
                .massCart(MASS_CART).massPole(MASS_POLE).massTotal(MASS_CART + MASS_POLE)
                .length(LENGTH).massPoleTimesLength(MASS_POLE*LENGTH)
                .forceMagnitude(FORCE_MAGNITUDE)
                .tau(TAU)
                .angleMax(ANGLE_MAX).angleDotMax(ANGLE_DOT_MAX).xMax(X_MAX).xDotMax(X_DOT_MAX)
                .maxNofSteps(MAX_NOF_STEPS)
                .rewardNonFail(1).rewardFail(0)
                .build();
    }

    public static ParametersPole newWithMaxNofSteps(int maxNofSteps) {
        return ParametersPole.builder()
                .g(G).pi(PI)
                .massCart(MASS_CART).massPole(MASS_POLE).massTotal(MASS_CART + MASS_POLE)
                .length(LENGTH).massPoleTimesLength(MASS_POLE*LENGTH)
                .forceMagnitude(FORCE_MAGNITUDE)
                .tau(TAU)
                .angleMax(ANGLE_MAX).angleDotMax(ANGLE_DOT_MAX).xMax(X_MAX).xDotMax(X_DOT_MAX)
                .maxNofSteps(maxNofSteps)
                .rewardNonFail(1).rewardFail(0)
                .build();
    }



    public static ParametersPole newRewardNegativeFail() {
        return ParametersPole.builder()
                .g(G).pi(PI)
                .massCart(MASS_CART).massPole(MASS_POLE).massTotal(MASS_CART + MASS_POLE)
                .length(LENGTH).massPoleTimesLength(MASS_POLE*LENGTH)
                .forceMagnitude(FORCE_MAGNITUDE)
                .tau(TAU)
                .angleMax(ANGLE_MAX).angleDotMax(ANGLE_DOT_MAX).xMax(X_MAX).xDotMax(X_DOT_MAX)
                .maxNofSteps(MAX_NOF_STEPS)
                .rewardNonFail(0).rewardFail(-1)
                .build();
    }


}
