package policygradient.sink_the_ship;

import common.other.Counter;
import common.math.MathUtils;
import common.other.RandUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.domain.abstract_classes.Action;
import common.reinforcment_learning.value_classes.StepReturn;
import policy_gradient_problems.environments.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.environments.sink_the_ship.ShipSettings;
import policy_gradient_problems.environments.sink_the_ship.StateShip;
import policy_gradient_problems.environments.sink_the_ship.VariablesShip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

 class TestEnvironmentShip {

     static final int DIST_TOL = 100;
    EnvironmentShip environment;

    @BeforeEach
     void init() {
        environment=new EnvironmentShip(ShipSettings.newDefault());
    }

    @Test
     void whenNonZeroAngle_thenNonZeroDistanceProjectile() {
        double distance0=environment.calcDistanceProjectile(0d);
        double distance1=environment.calcDistanceProjectile(0.01);
        assertEquals(0,distance0);
        assertTrue(distance1>0);
    }

    @Test
     void whenState0_thenThereIsAngleGivingHit() {
        int state = 0;
        assertHitInState(state);
    }

    @Test
     void whenState1_thenThereIsAngleGivingHit() {
        int state = 1;
        assertHitInState(state);
    }

    private void assertHitInState(int state) {
        StepReturn<VariablesShip> sr;
        double angle;
        Counter counter=new Counter(10_000);
        do {
            //angle = RandUtils.getRandomDouble(-5,10);
            angle = RandUtils.getRandomDouble(0,1);

            sr=environment.step(StateShip.newFromPos(state), Action.ofDouble(angle));
            counter.increase();

            System.out.println("angle = " + angle);
            System.out.println("sr.reward() = " + sr.reward());
        } while (!sr.isTerminal() && !counter.isExceeded());



        Double expectedDistance = EnvironmentShip.DISTANCE_TO_SHIP_MAP.get(state);
        double actualDistance = environment.calcDistanceProjectile(angle);

        Assertions.assertTrue(MathUtils.isInRange(angle,0,1));
        Assertions.assertTrue(sr.isTerminal());
        Assertions.assertEquals(expectedDistance, actualDistance, DIST_TOL);
    }


}
