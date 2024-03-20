package policy_gradient_problems.environments.cart_pole;

import lombok.Builder;
import org.apache.commons.math3.util.Pair;

import java.util.List;

import static common.MyFunctions.*;

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
    static final double ANGLE_MAX = 12 * 2 * PI / 360, X_MAX = 2;
    static final double G = 9.81, TAU = 0.02;
    static final double FORCE_MAGNITUDE = 10;
    static final int MAX_NOF_STEPS = 100;
    static final double ANGLE_DOT_MAX = 0.3;
    static final double X_DOT_MAX = 10;
    static final double REWARD = 1d, REWARD_FAIL = 0d;

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
                defaultIfNullDouble.apply(rewardNonFail, REWARD),
                defaultIfNullDouble.apply(rewardFail, REWARD_FAIL));
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

    public  List<Pair<Double, Double>> minMaxStatePairList() {
        return List.of(
                Pair.create(-angleMax(), angleMax()),
                Pair.create(-xMax(), xMax()),
                Pair.create(-angleDotMax(), angleDotMax()),
                Pair.create(-xDotMax(), xDotMax()));
    }


}
