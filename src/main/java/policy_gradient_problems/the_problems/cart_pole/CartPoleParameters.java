package policy_gradient_problems.the_problems.cart_pole;

import lombok.Builder;

@Builder
public record CartPoleParameters (
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
        double xMax,
        double maxNofSteps,
        double rewardNonFail,
        double rewardFail)   {

    static final double MASS_POLE = 0.1, MASS_CART = 1;
    static final double LENGTH = 0.5;
    static final double PI = 3.141592;

    public static CartPoleParameters newDefault() {
        return CartPoleParameters.builder()
                .g(9.81).pi(PI)
                .massCart(MASS_CART).massPole(MASS_POLE).massTotal(MASS_CART + MASS_POLE)
                .length(LENGTH)
                .massPoleTimesLength(MASS_POLE*LENGTH)
                .forceMagnitude(10)
                .tau(0.02)
                .angleMax(12 * 2 * PI / 360)
                .xMax(2)
                .maxNofSteps(100)
                .rewardNonFail(1).rewardFail(0)
                .build();
    }

}
