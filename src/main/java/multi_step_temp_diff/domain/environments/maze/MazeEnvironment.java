package multi_step_temp_diff.domain.environments.maze;

import common.MySetUtils;
import lombok.SneakyThrows;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.environment_valueobj.MazeEnvironmentSettings;
import org.apache.commons.math3.util.Pair;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    public enum PositionType {
        otherCell, wall, obstacle, goal
    }

    public static final int ACTION_UP=0, ACTION_R=1,ACTION_DOWN=2,ACTION_L=3;
    public static final MazeEnvironmentSettings settings=MazeEnvironmentSettings.getDefault();

    static Map<BiPredicate<Integer,Integer>, Supplier<PositionType>> stateAfterStepTable;
    BiPredicate<Integer,Integer> isWall = (x, y) ->  x<0 || x>=settings.nofCols()  || y<0 || y>= settings.nofRows();

    public static BiPredicate<Integer,Integer> isObstacle= (x, y) ->
            settings.obstaclePositions().stream().anyMatch(p -> Pair.create(x,y).equals(p));
    public static BiPredicate<Integer,Integer> isGoal= (x, y) -> settings.goalPos().equals(Pair.create(x,y));
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
        Predicate<Integer> isNonValidAction = (a) -> a > settings.nofActions() - 1;
        Predicate<StateInterface<MazeVariables> > isNonValidX = (s) -> MazeState.getX.apply(s) > settings.nofCols() - 1;
        Predicate<StateInterface<MazeVariables> > isNonValidY = (s) -> MazeState.getX.apply(s) > settings.nofRows() - 1;
        if (isNonValidAction.test(action) || isNonValidX.or(isNonValidY).test(state)) {
            throw new IllegalArgumentException("Non valid action or state. State = " + state+ ", action = " + action);
        }
    }

    private Pair<StateInterface<MazeVariables>, PositionType> getNewPosAndState(StateInterface<MazeVariables>  state,
                                                                                int action) {
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
            return settings.rewardCrash();
        } else if (stateAfterStep.equals(PositionType.goal)) {
            return settings.rewardGoal()+settings.rewardMove();
        } else {
            return settings.rewardMove();
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
        return MySetUtils.getSetFromRange(0,settings.nofActions());
    }

    @SneakyThrows
    @Override
    public Set<StateInterface<MazeVariables>> stateSet() {
        Set<StateInterface<MazeVariables>> stateSet = new HashSet<>();
        for (int x : MySetUtils.getSetFromRange(0, settings.nofCols())) {
            for (int y : MySetUtils.getSetFromRange(0, settings.nofRows())) {
                stateSet.add(MazeState.newFromXY(x, y));
            }
        }
        return stateSet;
    }

    /**
     * trash
     *     Set<Pair<Integer,Integer>> obstaclePositions=new HashSet<>(asList(
     *             Pair.create(0,0),Pair.create(1,0),Pair.create(2,0),Pair.create(3,0), Pair.create(4,0),
     *             Pair.create(0,1),Pair.create(1,1),Pair.create(2,1),Pair.create(3,1), Pair.create(4,1),
     *             Pair.create(0,2),Pair.create(1,2),Pair.create(2,2),Pair.create(3,2), Pair.create(4,2),
     *             Pair.create(0,3),Pair.create(1,3),Pair.create(2,3),Pair.create(3,3), Pair.create(4,3)
     *           //  Pair.create(0,4),Pair.create(1,4),Pair.create(2,4),
     *           //  Pair.create(0,5),Pair.create(1,5),Pair.create(2,5)
     *             ));
     */

}
