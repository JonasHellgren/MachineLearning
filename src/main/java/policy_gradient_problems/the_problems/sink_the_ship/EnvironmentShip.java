package policy_gradient_problems.the_problems.sink_the_ship;
import common.MathUtils;
import common.RandUtils;

import java.util.Map;
import java.util.Set;

public class EnvironmentShip {
    public static final Set<Integer> STATES =Set.of(0,1);

    final double ANGLE_MAX = Math.PI / 4,CONSTANCE_OF_GRAVITY = 9.81;
    final double SPEED_PROJECTILE_MPS = 150.0, DEVIATION_MAX_METER = 20d;

    public static final Map<Integer,Double> DISTANCE_TO_SHIP_MAP = Map.of(0,1000d, 1,2000d);  //<state, distance>
    final double REWARD_HIT = 1, REWARD_MISS = 0;
    final boolean IS_TERMINAL = false;

    public StepReturnShip step(int state, double action) {
        boolean isHit=isHitting(state,action);
        double reward = getReward(isHit);
        int stateNew=getStateNew(state);
        return new StepReturnShip(stateNew, IS_TERMINAL, isHit, reward);
    }

    public double calcDistanceProjectile(double normalizedAngle) {
        double angleInRadians=ANGLE_MAX*normalizedAngle;
        return SPEED_PROJECTILE_MPS
                *Math.cos(angleInRadians)*2*SPEED_PROJECTILE_MPS
                *Math.sin(angleInRadians)/CONSTANCE_OF_GRAVITY;
    }

    public static  Integer getRandomState() {
        int randIndex=RandUtils.getRandomIntNumber(0,nofStates());
        return STATES.stream().toList().get(randIndex);
    }

    public static  Integer nofStates() {
        return STATES.size();
    }

    private  double getReward(boolean isHit) {
        return isHit ? REWARD_HIT : REWARD_MISS;
    }

    private boolean isHitting(int state, double normalizedAngle) {
        double distanceProjectile=calcDistanceProjectile(MathUtils.clipBetwenZeroAndOne(normalizedAngle));
        double distanceToShip=DISTANCE_TO_SHIP_MAP.get(state);
        double projectileShipDistanceDeviation=Math.abs(distanceToShip-distanceProjectile);
        return projectileShipDistanceDeviation< DEVIATION_MAX_METER;
    }

    private int getStateNew(int state) {
        return state;
    }


}
