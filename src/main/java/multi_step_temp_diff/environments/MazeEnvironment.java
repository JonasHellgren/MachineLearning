package multi_step_temp_diff.environments;

import common.Conditionals;
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
 *
 * -------------------------------------
 * |  S  |       |       |       |       |
 * -------------------------------------
 * |     |       |  X    |       |       |
 * -------------------------------------
 * |     |  X    |  X    |  X    |       |
 * -------------------------------------
 * |     |       |       |       |       |
 * -------------------------------------
 * |     |  X    |  X    |  X    |       |
 * -------------------------------------
 * |     |       |       |       |  G    |
 * -------------------------------------
 *
 */

public class MazeEnvironment implements EnvironmentInterface<MazeVariables> {

    public static final int NOF_COLS = 5, NOF_ROWS = 6;
    public static final int NOF_ACTIONS = 4;

    enum StepState {
        otherCell, wall, obstacle, goal
    }

    Map<BiPredicate<Integer,Integer>, Supplier<StepState>> stateTable;
    BiPredicate<Integer,Integer> isWall = (x, y) ->  x<0 || x>=NOF_COLS  || y<0 || y>=NOF_ROWS;
    Set<Pair<Integer,Integer>> obstaclePositions=new HashSet<>(asList(
             Pair.create(2,1)
            ,Pair.create(1,2),Pair.create(2,2),Pair.create(3,2)
            ,Pair.create(1,3),Pair.create(2,3),Pair.create(3,3)));
    BiPredicate<Integer,Integer> isObstacle= (x, y) ->
            obstaclePositions.stream().anyMatch(p -> Pair.create(x,y).equals(p));
    BiPredicate<Integer,Integer> isGoal= (x, y) -> Pair.create(4,5).equals(Pair.create(x,y));
    Map<Integer, Integer> actionDeltaXmap = Map.of(0,0,1,1,2,0,3,-1);
    Map<Integer, Integer> actionDeltaYmap = Map.of(0,1,1,0,2,-1,3,0);
    BiFunction<Integer,Integer,Integer> newX=(x,a) -> x+actionDeltaXmap.getOrDefault(a,x);
    BiFunction<Integer,Integer,Integer> newY=(y,a) -> y+actionDeltaYmap.getOrDefault(a,y);

    public MazeEnvironment() {
        stateTable = new HashMap<>();
        stateTable.put( (s,p) -> isWall.test(s,p) ,() -> StepState.wall);
        stateTable.put( (s,p) -> isObstacle.test(s,p) ,() -> StepState.obstacle);
        stateTable.put( (s,p) -> isGoal.test(s,p) ,() -> StepState.goal);
    }

    @Override
    public StepReturn<MazeVariables> step(StateInterface<MazeVariables> state, int action) {
        throwIfBadArgument(state,action);
        final Pair<StateInterface<MazeVariables>,StepState> pair = getNewPosAndState(state, action);
        return StepReturn.<MazeVariables>builder()
                .isNewStateTerminal(pair.getSecond().equals(StepState.goal))
                .newState(pair.getFirst())
                .reward(getReward(pair.getSecond()))
                .build();
    }

    private void throwIfBadArgument(StateInterface<MazeVariables> state, int action) {
        Predicate<Integer> isNonValidAction = (a) -> a > NOF_ACTIONS - 1;
        Predicate<StateInterface<MazeVariables> > isNonValidX = (s) -> MazeState.getX.apply(s) > NOF_COLS - 1;
        Predicate<StateInterface<MazeVariables> > isNonValidY = (s) -> MazeState.getX.apply(s) > NOF_ROWS - 1;
        Conditionals.executeIfTrue(isNonValidAction.test(action) || isNonValidX.or(isNonValidY).test(state), () ->
        {
            throw new IllegalArgumentException("Non valid action or state. State = " + state+ ", action = " + action);
        });
    }

    private Pair<StateInterface<MazeVariables>,StepState> getNewPosAndState(StateInterface<MazeVariables>  state, int action) {
        int x = MazeState.getX.apply(state);
        int xNew = newX.apply(x,action);
        int y = MazeState.getY.apply(state);
        int yNew = newY.apply(y,action);
        StepState stateAfterStep = getStepState(xNew, yNew);
        StateInterface<MazeVariables> stateAfter=stateAfterStep.equals(StepState.wall) || stateAfterStep.equals(StepState.obstacle)
                ? MazeState.newFromXY(x,y)   //no motion if entering eg wall
                : MazeState.newFromXY(xNew,yNew);  //otherCell or goal
        return Pair.create(stateAfter,stateAfterStep);
    }

    private double getReward(StepState stateAfterStep) {
        if (stateAfterStep.equals(StepState.wall) || stateAfterStep.equals(StepState.obstacle)) {
            return  -1;
        } else if (stateAfterStep.equals(StepState.goal)) {
            return  100;
        } else {
            return 0;
        }
    }

    public StepState getStepState(Integer x, Integer y) {
        List<Supplier<StepState>> fcnList=stateTable.entrySet().stream()
                .filter(e -> e.getKey().test(x,y)).map(Map.Entry::getValue)
                .collect(Collectors.toList());
        if (fcnList.size()>1) {
            throw new RuntimeException("Multiple matching rules, nof ="+fcnList.size()+". Applying random.");
        }
        if (fcnList.size()==0) {
            return StepState.otherCell;
        }
        return fcnList.get(0).get();
    }


    @Override
    public boolean isTerminalState(StateInterface<MazeVariables> state) {
        return false;
    }

    @Override
    public Set<Integer> actionSet() {
        return null;
    }

    @Override
    public Set<StateInterface<MazeVariables>> stateSet() {
        return null;
    }
}
