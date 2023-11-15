package policygradient.sink_the_ship;

import common.Counter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.sink_the_ship.StepReturnShip;

import static common.RandUtils.randomNumberBetweenZeroAndOne;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestEnvironmentShip {

    public static final int DIST_TOL = 100;
    EnvironmentShip environment;

    @BeforeEach
    public void init() {
        environment=new EnvironmentShip();
    }

    @Test
    public void whenNonZeroAngle_thenNonZeroDistanceProjectile() {
        double distance0=environment.calcDistanceProjectile(0d);
        double distance1=environment.calcDistanceProjectile(0.01);
        assertEquals(0,distance0);
        assertTrue(distance1>0);
    }

    @Test
    public void whenState0_thenThereIsAngleGivingHit() {
        int state = 0;
        assertHitInState(state);
    }

    @Test
    public void whenState1_thenThereIsAngleGivingHit() {
        int state = 1;
        assertHitInState(state);
    }

    private void assertHitInState(int state) {
        StepReturnShip sr;
        double normAngle;
        Counter counter=new Counter(10_000);
        do {
            normAngle = randomNumberBetweenZeroAndOne();
            sr=environment.step(state, normAngle);
            counter.increase();
        } while (!sr.isHit() && !counter.isExceeded());

        System.out.println("normAngle = " + normAngle);

        Double expectedDistance = EnvironmentShip.DISTANCE_TO_SHIP_MAP.get(state);
        double actualDistance = environment.calcDistanceProjectile(normAngle);
        Assertions.assertTrue(sr.isHit());
        Assertions.assertEquals(expectedDistance, actualDistance, DIST_TOL);
    }


}
