package monte_carlo_tree_search.domains.elevator;

import common.MathUtils;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 *  Influenced by https://busoniu.net/files/papers/ifac08-elevators.pdf
 *
 *
 *    |_30__|____ floor 3
 *    |_29__|
 *     :
 *     :
 *    |_21__|
 *    |_20__|____ floor 2
 *    |_19__|
 *     :
 *     :
 *    |_11__|
 *    |_10__|____ floor 1
 *    |_09__|
 *     :
 *     :
 *    |_01__|
 *    |_00__|____ floor 0
 *
 *   states={speed,pos, nPersonsInElevator, nPersonsWaitingFloor1,..,nPersonsWaitingFloor3,SoE}
 *
 *   action = speed
 *
 *   d/dt pos= speed
 *   d/dt SoE=  powerCharge  (pos & speed == 0)
 *              powerUp  (speed is positive)
 *              powerDown (speed is negative)
                0 (speed is zero)
 *
 *   nPersonsInElevator = 0 if floor = 0, increased if entering floor with waiting person(s)
 *   nPersonsWaitingFloor i = zero if pos corresponds to that floor
 *
 *   reward=sum (nPersonsWaitingFloor i)
 *
 *
 *
 */


public class EnvironmentElevator implements EnvironmentGenericInterface<VariablesElevator, Integer>  {
    private static final int MIN_POS = 0;
    private static final int MAX_POS = 30;
    private static final int NOF_POS_BETWEEN_FLOORS = 10;
    private static final int BOTTOM_FLOOR = 0;
    private static final Double POWER_CHARGE = 3_000d;
    private static final Double POWER_STILL = 0d;
    private static final Double POWER_MOVING_UP = -1000d;
    private static final Double POWER_MOVING_DOWN = 500d;
    private static final Double CAPACITY_BATTERY = 1000d * 60d;
    private static final double SOC_LOW = 0.2;
    private static final int REWARD_FAIL = -100;


    public static EnvironmentGenericInterface<VariablesElevator, Integer> newDefault() {
        return new EnvironmentElevator();
    }

    @Override
    public StepReturnGeneric<VariablesElevator> step(ActionInterface<Integer> action, StateInterface<VariablesElevator> state) {

        int speed=action.getValue();
        int newPos= MathUtils.clip(state.getVariables().pos+speed, MIN_POS, MAX_POS);
        int nPersonsInElevator=calcNofPersonsInElevator(newPos,state);
        List<Integer> nPersonsWaiting=updateNofPersonsWaiting(state);
        double newSoE= updateSoE(newPos,speed,state);

        StateInterface<VariablesElevator> newState= new StateElevator(VariablesElevator.builder()
                .speed(speed)
                .pos(newPos)
                .nPersonsInElevator(nPersonsInElevator)
                .nPersonsWaiting(nPersonsWaiting)
                .SoE(newSoE)
                .build());
        double nonFailReward=-nPersonsWaiting.stream().mapToInt(Integer::intValue).sum();

        return StepReturnGeneric.<VariablesElevator>builder()
                .newState(newState)
                .isFail(isFailsState(newState))
                .isTerminal(isFailsState(newState))
                .reward((isFailsState(newState))? REWARD_FAIL : nonFailReward)
                .build();
    }

    @NotNull
    private Optional<Integer> getFloor(int pos) {
        return (pos % NOF_POS_BETWEEN_FLOORS == 0)
                ? Optional.of(pos / NOF_POS_BETWEEN_FLOORS)
                : Optional.empty();
    }

    boolean isFailsState(StateInterface<VariablesElevator> newState) {
        //TODO  nPersonsWaiting > 20
        return newState.getVariables().SoE< SOC_LOW;
    }

    int calcNofPersonsInElevator(int newPos, StateInterface<VariablesElevator> state) {
        List<Integer> nPersonsWaiting=state.getVariables().nPersonsWaiting;
        int nPersonsInElevator=state.getVariables().nPersonsInElevator;
        Optional<Integer> floor = getFloor(newPos);
        Predicate<Optional<Integer>> isPersonsEnteringElevator= f -> f.isPresent() && f.get()!= BOTTOM_FLOOR;
        Predicate<Optional<Integer>> isPersonsLeavingElevator= f -> f.isPresent() && f.get()== BOTTOM_FLOOR;

        if   (isPersonsEnteringElevator.test(floor)) {
            return nPersonsInElevator + nPersonsWaiting.get(floor.orElseThrow());
        }

        if (isPersonsLeavingElevator.test(floor)) {
            return 0;
        }

        return nPersonsInElevator;
    }

    List<Integer>  updateNofPersonsWaiting(StateInterface<VariablesElevator> state) {
        List<Integer> nPersonsWaiting=state.getVariables().nPersonsWaiting;

        //TODO - define logic here

        return nPersonsWaiting;
    }

    double updateSoE(int newPos, int speed,StateInterface<VariablesElevator> state) {
        Optional<Integer> floor = getFloor(newPos);

        //todo - cleanup by list of Pair<Predicate, Double>
        BiPredicate<Optional<Integer>,Integer> isStandingStillBottomFloor =
                (f,s) -> f.isPresent() && f.get()== BOTTOM_FLOOR && s==0;
        BiPredicate<Optional<Integer>,Integer> isStandingStillNotBottomFloor =
                (f,s) -> f.isPresent() && f.get()!= BOTTOM_FLOOR && s==0;
        Predicate<Integer> isMovingUp= s -> s>0;
        Predicate<Integer> isMovingDown= s -> s<0;

        Optional<Double> power=Optional.empty();
        if (isStandingStillBottomFloor.test(floor,newPos)) {
            power= Optional.of(POWER_CHARGE);
        }
        if (isStandingStillNotBottomFloor.test(floor,speed)) {
            power= Optional.of(POWER_STILL);
        }
        if (isMovingUp.test(speed)) {
            power= Optional.of(POWER_MOVING_UP);
        }

        if (isMovingDown.test(speed)) {
            power= Optional.of(POWER_MOVING_DOWN);
        }

        return state.getVariables().SoE+power.orElseThrow()/CAPACITY_BATTERY;

    }


}
