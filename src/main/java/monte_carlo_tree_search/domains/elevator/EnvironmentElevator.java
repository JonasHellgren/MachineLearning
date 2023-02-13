package monte_carlo_tree_search.domains.elevator;

import common.MathUtils;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
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
    private static final double REWARD_FAIL = -100;
    private static final Integer NOF_FLOORS = 3;
    private static final int BIG = Integer.MAX_VALUE;

    BiPredicate<Integer, Optional<Integer>> isStill = (s, f) -> s == 0;
    BiPredicate<Integer, Optional<Integer>> isBottomFloor= (s, f) -> f.equals(Optional.of(BOTTOM_FLOOR));
    BiPredicate<Integer, Optional<Integer>> isNotBottomFloor= isBottomFloor.negate();
    BiPredicate<Integer, Optional<Integer>> isPersonsEnteringElevator = (s,f) ->  isStill.and(isNotBottomFloor).test(s,f);
    BiPredicate<Integer, Optional<Integer>> isPersonsLeavingElevator = (s, f) -> isStill.and(isBottomFloor).test(s,f);

    NofPersonsWaitingUpdater nofPersonsWaitingUpdater;
    Map<BiPredicate<Integer,Optional<Integer>>, Supplier<Double>> liftingPowerTable;  //speed,floor -> power


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
    public StepReturnGeneric<VariablesElevator> step(ActionInterface<Integer> action, StateInterface<VariablesElevator> state) {

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
        double nonFailReward = -nPersonsWaiting.stream().mapToInt(Integer::intValue).sum();

        return StepReturnGeneric.<VariablesElevator>builder()
                .newState(newState)
                .isFail(isFailsState(newState))
                .isTerminal(isFailsState(newState))
                .reward((isFailsState(newState)) ? REWARD_FAIL : nonFailReward)
                .build();
    }

    @NotNull
    private Optional<Integer> getFloor(int pos) {
        return (pos % NOF_POS_BETWEEN_FLOORS == 0)
                ? Optional.of(pos / NOF_POS_BETWEEN_FLOORS)
                : Optional.empty();
    }

    public boolean canPersonLeavingOrEnter(StateInterface<VariablesElevator> state) {
        int newSpeed = state.getVariables().speed;
        int newPos = state.getVariables().pos;
        Optional<Integer> floor = getFloor(newPos);

        return isPersonsEnteringElevator.or(isPersonsLeavingElevator).test(newSpeed, floor);
    }


    boolean isFailsState(StateInterface<VariablesElevator> newState) {
        //TODO  nPersonsWaiting > 20
        return newState.getVariables().SoE < SOC_LOW;
    }

    int calcNofPersonsInElevator(int newSpeed, int newPos, StateInterface<VariablesElevator> state) {
        List<Integer> nPersonsWaiting = state.getVariables().nPersonsWaiting;
        int nPersonsInElevator = state.getVariables().nPersonsInElevator;
        Optional<Integer> floor = getFloor(newPos);

        if (isPersonsEnteringElevator.test(newSpeed, floor)) {
            return nPersonsInElevator + nPersonsWaiting.get(floor.orElseThrow() - 1);
        }

        if (isPersonsLeavingElevator.test(newSpeed, floor)) {
            return 0;
        }

        return nPersonsInElevator;
    }

    List<Integer> updateNofPersonsWaiting(int newSpeed, int newPos, StateInterface<VariablesElevator> state) {

        state = nofPersonsWaitingUpdater.update(state);
        List<Integer> nPersonsWaiting = state.getVariables().nPersonsWaiting;
        Optional<Integer> floor = getFloor(newPos);

        if (isPersonsEnteringElevator.test(newSpeed, floor)) {
            nPersonsWaiting.set(floor.orElseThrow() - 1, 0);
        }

        return nPersonsWaiting;
    }

    double updateSoE(int newPos, int newSpeed, StateInterface<VariablesElevator> state) {
        return MathUtils.clip(state.getVariables().SoE + getPower(newPos, newSpeed)/ CAPACITY_BATTERY,0,1);
    }

    private Double getPower(int newPos, int newSpeed) {
        Optional<Integer> floor = getFloor(newPos);
        List<Supplier<Double>> fcnList= liftingPowerTable.entrySet().stream()
                .filter(e -> e.getKey().test(newSpeed,floor))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (fcnList.size()!=1) {
            throw new RuntimeException("No/multiple matching rules, nof ="+fcnList.size());
        }
        return fcnList.get(0).get();
    }

    private Map<BiPredicate<Integer,Optional<Integer>>, Supplier<Double>> constructPowerRules() {
        BiPredicate<Integer,Optional<Integer>> isStandingStillBottomFloor =
                (s, f) -> isStill.and(isBottomFloor).test(s,f);
        BiPredicate<Integer,Optional<Integer>> isStandingStillNotBottomFloor =
                (s, f) -> isStill.and(isNotBottomFloor).test(s,f);
        BiPredicate<Integer,Optional<Integer>> isMovingUp = (s,f) -> s > 0;
        BiPredicate<Integer,Optional<Integer>> isMovingDown = (s,f) -> s < 0;

        Map<BiPredicate<Integer,Optional<Integer>>, Supplier<Double>> decisionTable = new HashMap<>();
        decisionTable.put(isStandingStillBottomFloor, () -> POWER_CHARGE);
        decisionTable.put(isStandingStillNotBottomFloor, () -> POWER_STILL);
        decisionTable.put(isMovingUp, () -> POWER_MOVING_UP);
        decisionTable.put(isMovingDown, () -> POWER_MOVING_DOWN);
        return decisionTable;
    }


}
