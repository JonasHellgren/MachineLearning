package monte_carlo_tree_search.domains.elevator;

import common.MathUtils;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import org.apache.arrow.flatbuf.Int;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Influenced by https://busoniu.net/files/papers/ifac08-elevators.pdf
 * <p>
 * <p>
 * |_30__|____ floor 3
 * |_29__|
 * :
 * :
 * |_21__|
 * |_20__|____ floor 2
 * |_19__|
 * :
 * :
 * |_11__|
 * |_10__|____ floor 1
 * |_09__|
 * :
 * :
 * |_01__|
 * |_00__|____ floor 0
 * <p>
 * states={speed,pos, nPersonsInElevator, nPersonsWaitingFloor1,..,nPersonsWaitingFloor3,SoE}
 * <p>
 * action = speed
 * <p>
 * d/dt pos= speed
 * d/dt SoE=  powerCharge  (pos & speed == 0)
 * powerUp  (speed is positive)
 * powerDown (speed is negative)
 * 0 (speed is zero)
 * <p>
 * nPersonsInElevator = 0 if floor = 0, increased if entering floor with waiting person(s)
 * nPersonsWaitingFloor i = zero if pos corresponds to that floor
 * <p>
 * reward=sum (nPersonsWaitingFloor i)
 */


public class EnvironmentElevator implements EnvironmentGenericInterface<VariablesElevator, Integer> {
    public static final int MIN_POS = 0;
    public static final int MAX_POS = 30;
    private static final Integer NOF_POS_BETWEEN_FLOORS = 10;
    private static final int BOTTOM_FLOOR = 0;
    private static final Double POWER_CHARGE = 3_000d;
    private static final Double POWER_STILL = -100d;
    private static final Double POWER_MOVING_UP = -1000d;
    private static final Double POWER_MOVING_DOWN = 500d;
    private static final Double CAPACITY_BATTERY = 1000d * 60d;
    private static final double SOC_LOW = 0.2;
    public static final int MAX_NOF_PERSONS_IN_ELEVATOR=10;
    private static final double REWARD_FAIL = -100;
    private static final Integer NOF_FLOORS = 3;
    private static final int BIG = Integer.MAX_VALUE;

    public static  BiPredicate<Integer,Integer> isAtTop = (s, p) -> p == MAX_POS;
    public static BiPredicate<Integer, Integer> isAtFloor = (s, p) -> (p % NOF_POS_BETWEEN_FLOORS == 0);
    public static BiPredicate<Integer, Integer> isBottomFloor = (s, p) -> p.equals(BOTTOM_FLOOR);
    public static BiPredicate<Integer, Integer> isNotBottomFloor = isBottomFloor.negate();
    public static BiPredicate<Integer, Integer> isStill = (s, p) -> s == 0;
    public static BiPredicate<Integer, Integer> isPersonsEnteringElevator = (s, p) ->  isStill.and(isAtFloor).and(isNotBottomFloor).test(s,p);
    public static BiPredicate<Integer, Integer> isPersonsLeavingElevator = (s, p) -> isStill.and(isBottomFloor).test(s,p);

    NofPersonsWaitingUpdater nofPersonsWaitingUpdater;
    Map<BiPredicate<Integer,Integer>, Supplier<Double>> liftingPowerTable;  //speed,floor -> power


    public EnvironmentElevator(NofPersonsWaitingUpdater nofPersonsWaitingUpdater) {
        this.nofPersonsWaitingUpdater = nofPersonsWaitingUpdater;
        this.liftingPowerTable = constructPowerRules();
    }

    public static EnvironmentGenericInterface<VariablesElevator, Integer> newDefault() {
        NofPersonsWaitingUpdater nofPersonsWaitingUpdater = new NofPersonsWaitingUpdater(Arrays.asList(BIG, BIG, BIG));
        return new EnvironmentElevator(nofPersonsWaitingUpdater);
    }

    public static EnvironmentGenericInterface<VariablesElevator, Integer>
    newFromStepBetweenAddingNofWaiting(List<Integer> stepList) {

        if (stepList.size() != NOF_FLOORS) {
            throw new IllegalArgumentException("Bad size stepList");
        }

        NofPersonsWaitingUpdater nofPersonsWaitingUpdater = new NofPersonsWaitingUpdater(stepList);
        return new EnvironmentElevator(nofPersonsWaitingUpdater);
    }

    @Override
    public StepReturnGeneric<VariablesElevator> step(ActionInterface<Integer> action,
                                                     final StateInterface<VariablesElevator> state) {

        int newSpeed = action.getValue();
        int newPos = MathUtils.clip(state.getVariables().pos + newSpeed, MIN_POS, MAX_POS);
        int nPersonsInElevator = calcNofPersonsInElevator(newSpeed, newPos, state);
        List<Integer> nPersonsWaiting = updateNofPersonsWaiting(newSpeed, newPos, state);
        double newSoE = updateSoE(newPos, newSpeed, state);

        StateInterface<VariablesElevator> newState = new StateElevator(VariablesElevator.builder()
                .speed(newSpeed)
                .pos(newPos)
                .nPersonsInElevator(nPersonsInElevator)
                .nPersonsWaiting(nPersonsWaiting)
                .SoE(newSoE)
                .build());
        double nonFailReward = -nPersonsWaiting.stream().mapToInt(Integer::intValue).sum(); //-nPersonsInElevator/2;

        return StepReturnGeneric.<VariablesElevator>builder()
                .newState(newState)
                .isFail(isFailsState(newState))
                .isTerminal(isFailsState(newState))
                .reward((isFailsState(newState)) ? REWARD_FAIL : nonFailReward)
                .build();
    }

    public static Optional<Integer> getFloor(int pos) {
        return (pos % NOF_POS_BETWEEN_FLOORS == 0)
                ? Optional.of(pos / NOF_POS_BETWEEN_FLOORS)
                : Optional.empty();
    }


    boolean isFailsState(StateInterface<VariablesElevator> newState) {
        //TODO  nPersonsWaiting > 20
        return newState.getVariables().SoE < SOC_LOW && newState.getVariables().nPersonsInElevator<=MAX_NOF_PERSONS_IN_ELEVATOR;
    }

    int calcNofPersonsInElevator(int newSpeed, int newPos, StateInterface<VariablesElevator> state) {
        List<Integer> nPersonsWaiting = state.getVariables().nPersonsWaiting;
        int nPersonsInElevator = state.getVariables().nPersonsInElevator;

        if (isPersonsEnteringElevator.test(newSpeed, newPos)) {
            Optional<Integer> floor = getFloor(newPos);
            return nPersonsInElevator + nPersonsWaiting.get(floor.orElseThrow() - 1);
        }

        if (isPersonsLeavingElevator.test(newSpeed, newPos)) {
            return 0;
        }

        return nPersonsInElevator;
    }

    List<Integer> updateNofPersonsWaiting(int newSpeed, int newPos, StateInterface<VariablesElevator> state) {
        nofPersonsWaitingUpdater.update(state);
        removePersonsWaitingAtFloorIfElevatorPass(newSpeed, newPos, state);
        return state.getVariables().nPersonsWaiting;
    }

    private void removePersonsWaitingAtFloorIfElevatorPass(int newSpeed, int newPos, StateInterface<VariablesElevator> state) {
        List<Integer> nPersonsWaiting = state.getVariables().nPersonsWaiting;

        if (isPersonsEnteringElevator.test(newSpeed, newPos)) {
            Optional<Integer> floor = getFloor(newPos);
            nPersonsWaiting.set(floor.orElseThrow() - 1, 0);
        }
    }

    double updateSoE(int newPos, int newSpeed, StateInterface<VariablesElevator> state) {
        return MathUtils.clip(state.getVariables().SoE + getPower(newPos, newSpeed)/ CAPACITY_BATTERY,0,1);
    }

    private Double getPower(int newPos, int newSpeed) {
        List<Supplier<Double>> fcnList= liftingPowerTable.entrySet().stream()
                .filter(e -> e.getKey().test(newSpeed,newPos))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (fcnList.size()!=1) {
            throw new RuntimeException("No/multiple matching rules, nof ="+fcnList.size());
        }
        return fcnList.get(0).get();
    }

    private Map<BiPredicate<Integer,Integer>, Supplier<Double>> constructPowerRules() {
        BiPredicate<Integer,Integer> isStandingStillBottomFloor =
                (s, p) -> isStill.and(isBottomFloor).test(s,p);
       // (s, p) -> isBottomFloor.test(s,p);
        BiPredicate<Integer,Integer> isStandingStillNotBottomFloor =
                (s, p) -> isStill.and(isNotBottomFloor).test(s,p);
        BiPredicate<Integer,Integer> isMovingUp = (s,p) -> s > 0;
        BiPredicate<Integer,Integer> isMovingDown = (s,p) -> s < 0;

        Map<BiPredicate<Integer,Integer>, Supplier<Double>> decisionTable = new HashMap<>();
        decisionTable.put(isStandingStillBottomFloor, () -> POWER_CHARGE);
        decisionTable.put(isStandingStillNotBottomFloor, () -> POWER_STILL);
        decisionTable.put(isMovingUp, () -> POWER_MOVING_UP);
        decisionTable.put(isMovingDown, () -> POWER_MOVING_DOWN);
        return decisionTable;
    }


}
