package multi_step_temp_diff.environments;

import common.SetUtils;
import lombok.SneakyThrows;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
import org.apache.commons.math3.util.Pair;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import static java.util.Arrays.*;

/**
 * actions: 0=up, 1=right, 2=down, 3=left

 * -------------------------------------
 * |     |       |       |       |G=(4,5) |
 * -------------------------------------
 * |     |       |  X    |       |        |
 * -------------------------------------
 * |     |  X    |  X    |  X    |        |
 * -------------------------------------
 * |     |       |       |       |        |
 * -------------------------------------
 * |     |  X    |  X    |  X    |        |
 * -------------------------------------
 * |(0,0)|       |       |       |        |
 * -------------------------------------
 *
 */

public class MazeEnvironment implements EnvironmentInterface<MazeVariables> {

    public static final int NOF_COLS = 5, NOF_ROWS = 6;
    public static final int NOF_ACTIONS = 4;
    public static final int ACTION_UP=0, ACTION_R=1,ACTION_DOWN=2,ACTION_L=3;
    public static final double REWARD_CRASH = -10, REWARD_GOAL = 100, REWARD_MOVE = -1;

    public enum PositionType {
        otherCell, wall, obstacle, goal
    }

    static Map<BiPredicate<Integer,Integer>, Supplier<PositionType>> stateAfterStepTable;
    BiPredicate<Integer,Integer> isWall = (x, y) ->  x<0 || x>=NOF_COLS  || y<0 || y>=NOF_ROWS;
    Set<Pair<Integer,Integer>> obstaclePositions=new HashSet<>(asList(
             Pair.create(1,1),Pair.create(2,1),Pair.create(3,1)
            ,Pair.create(1,3),Pair.create(2,3),Pair.create(3,3)
            ,Pair.create(2,4)));
    BiPredicate<Integer,Integer> isObstacle= (x, y) ->
            obstaclePositions.stream().anyMatch(p -> Pair.create(x,y).equals(p));
    BiPredicate<Integer,Integer> isGoal= (x, y) -> Pair.create(4,5).equals(Pair.create(x,y));
    Map<Integer, Integer> actionDeltaXmap = Map.of(0,0,1,1,2,0,3,-1);
    Map<Integer, Integer> actionDeltaYmap = Map.of(0,1,1,0,2,-1,3,0);
    BiFunction<Integer,Integer,Integer> newX=(x,a) -> x+actionDeltaXmap.getOrDefault(a,x);
    BiFunction<Integer,Integer,Integer> newY=(y,a) -> y+actionDeltaYmap.getOrDefault(a,y);
    public static BiFunction<Integer,Integer,Boolean> isValidStartPosition=(x,y) ->
            getPositionType(x,y).equals(MazeEnvironment.PositionType.otherCell);

    public MazeEnvironment() {
        stateAfterStepTable = new HashMap<>();
        stateAfterStepTable.put( (s, p) -> isWall.test(s,p) ,() -> PositionType.wall);
        stateAfterStepTable.put( (s, p) -> isObstacle.test(s,p) ,() -> PositionType.obstacle);
        stateAfterStepTable.put( (s, p) -> isGoal.test(s,p) ,() -> PositionType.goal);
    }

    @Override
    public StepReturn<MazeVariables> step(StateInterface<MazeVariables> state, int action) {
        throwIfBadArgument(state,action);
        final Pair<StateInterface<MazeVariables>, PositionType> pair = getNewPosAndState(state, action);
        return StepReturn.<MazeVariables>builder()
                .isNewStateTerminal(pair.getSecond().equals(PositionType.goal))
                .newState(pair.getFirst())
                .reward(getReward(pair.getSecond()))
                .build();
    }

    private void throwIfBadArgument(StateInterface<MazeVariables> state, int action) {
        Predicate<Integer> isNonValidAction = (a) -> a > NOF_ACTIONS - 1;
        Predicate<StateInterface<MazeVariables> > isNonValidX = (s) -> MazeState.getX.apply(s) > NOF_COLS - 1;
        Predicate<StateInterface<MazeVariables> > isNonValidY = (s) -> MazeState.getX.apply(s) > NOF_ROWS - 1;
        if (isNonValidAction.test(action) || isNonValidX.or(isNonValidY).test(state)) {
            throw new IllegalArgumentException("Non valid action or state. State = " + state+ ", action = " + action);
        }
    }

    private Pair<StateInterface<MazeVariables>, PositionType> getNewPosAndState(StateInterface<MazeVariables>  state, int action) {
        int x = MazeState.getX.apply(state);
        int xNew = newX.apply(x,action);
        int y = MazeState.getY.apply(state);
        int yNew = newY.apply(y,action);
        PositionType stateAfterStep = getPositionType(xNew, yNew);
        StateInterface<MazeVariables> stateAfter=stateAfterStep.equals(PositionType.wall) || stateAfterStep.equals(PositionType.obstacle)
                ? MazeState.newFromXY(x,y)   //no motion if entering e.g. wall
                : MazeState.newFromXY(xNew,yNew);  //otherCell or goal
        return Pair.create(stateAfter,stateAfterStep);
    }

    private double getReward(PositionType stateAfterStep) {
        if (stateAfterStep.equals(PositionType.wall) || stateAfterStep.equals(PositionType.obstacle)) {
            return REWARD_CRASH;
        } else if (stateAfterStep.equals(PositionType.goal)) {
            return REWARD_GOAL+REWARD_MOVE;
        } else {
            return REWARD_MOVE;
        }
    }

    public static PositionType getPositionType(Integer x, Integer y) {
        List<Supplier<PositionType>> fcnList= stateAfterStepTable.entrySet().stream()
                .filter(e -> e.getKey().test(x,y)).map(Map.Entry::getValue)
                .collect(Collectors.toList());
        if (fcnList.size()>1) {
            throw new RuntimeException("Multiple matching rules, nof ="+fcnList.size());
        }
        if (fcnList.size()==0) {
            return PositionType.otherCell;
        }
        return fcnList.get(0).get();
    }


    @SneakyThrows
    @Override
    public boolean isTerminalState(StateInterface<MazeVariables> state) {
        return isGoal.test(MazeState.getX.apply(state),MazeState.getY.apply(state));
    }

    @SneakyThrows
    @Override
    public Set<Integer> actionSet() {
        return SetUtils.getSetFromRange(0,NOF_ACTIONS);
    }

    @SneakyThrows
    @Override
    public Set<StateInterface<MazeVariables>> stateSet() {
        Set<StateInterface<MazeVariables>> stateSet = new HashSet<>();
        for (int x : SetUtils.getSetFromRange(0, NOF_COLS)) {
            for (int y : SetUtils.getSetFromRange(0, NOF_ROWS)) {
                stateSet.add(MazeState.newFromXY(x, y));
            }
        }
        return stateSet;
    }

}
