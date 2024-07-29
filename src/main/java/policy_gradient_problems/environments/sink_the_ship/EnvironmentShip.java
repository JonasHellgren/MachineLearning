package policy_gradient_problems.environments.sink_the_ship;
import common.math.MathUtils;
import common.other.RandUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import policy_gradient_problems.domain.abstract_classes.Action;
import policy_gradient_problems.domain.abstract_classes.EnvironmentI;
import policy_gradient_problems.domain.abstract_classes.StateI;
import policy_gradient_problems.domain.value_classes.StepReturn;

import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public class EnvironmentShip implements EnvironmentI<VariablesShip> {
    public static final Set<Integer> POSITIONS =Set.of(0,1);
    public static final boolean IS_FAIL = false;

    static final double ANGLE_MAX = Math.PI / 4,CONSTANCE_OF_GRAVITY = 9.81;
    static final double SPEED_PROJECTILE_MPS = 150.0;


    public static final Map<Integer,Double> DISTANCE_TO_SHIP_MAP = Map.of(0,1000d, 1,2000d);  //<stateNew, distance>
    static final double REWARD_HIT = 1, REWARD_MISS = 0;

    @NonNull  ShipSettings shipSettings;

    public static EnvironmentShip newDefault() {
        return new EnvironmentShip(ShipSettings.newDefault());
    }

    @Override
    public StepReturn<VariablesShip> step(StateI<VariablesShip> state, Action action) {
        int pos = state.getVariables().pos();
        boolean isHit=isHitting(pos,action.asDouble());
        double reward = getReward(isHit);
        var stateNew=StateShip.newFromPos(getStateNew(pos));
        return new StepReturn<>(stateNew, IS_FAIL, isHit, reward);
    }

    public double calcDistanceProjectile(double normalizedAngle) {
        double angleInRadians=ANGLE_MAX*normalizedAngle;
        return SPEED_PROJECTILE_MPS
                *Math.cos(angleInRadians)*2*SPEED_PROJECTILE_MPS
                *Math.sin(angleInRadians)/CONSTANCE_OF_GRAVITY;
    }

    public static  Integer getRandomPos() {
        int randIndex=RandUtils.getRandomIntNumber(0,nofStates());
        return POSITIONS.stream().toList().get(randIndex);
    }

    public static  Integer nofStates() {
        return POSITIONS.size();
    }

    private  double getReward(boolean isHit) {
        return isHit ? REWARD_HIT : REWARD_MISS;
    }

    public boolean isHitting(int pos, double normalizedAngle) {
        double projectileShipDistanceDeviation = getProjectileShipDistanceDeviation(pos, normalizedAngle);
        return projectileShipDistanceDeviation< shipSettings.devMaxMeter();
    }

    private double getProjectileShipDistanceDeviation(int state, double normalizedAngle) {
        double distanceProjectile=calcDistanceProjectile(MathUtils.clipBetwenZeroAndOne(normalizedAngle));
        double distanceToShip=DISTANCE_TO_SHIP_MAP.get(state);
        return Math.abs(distanceToShip-distanceProjectile);
    }

    private int getStateNew(int state) {
        return state;
    }


}
