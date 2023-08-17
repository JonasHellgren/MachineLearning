package multi_step_temp_diff.domain.helpers_specific;

import common.ListUtils;
import multi_step_temp_diff.domain.agent_valueobj.AgentMazeTabularSettings;
import multi_step_temp_diff.domain.agent_abstract.AgentInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.environments.maze.MazeEnvironment;
import multi_step_temp_diff.domain.environments.maze.MazeState;
import multi_step_temp_diff.domain.environments.maze.MazeVariables;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

//todo exktahera Maze

public class ForkAndMazeHelper<S> {

    AgentInterface<S> agent;
    EnvironmentInterface<S> environment;

    public static final List<MazeState> STATES_MAZE_UPPER = List.of(
            MazeState.newFromXY(0, 5), MazeState.newFromXY(1, 5), MazeState.newFromXY(2, 5), MazeState.newFromXY(3, 5));
    public static final List<MazeState> STATES_MAZE_NEXT_UPPER = List.of(
            MazeState.newFromXY(0, 4), MazeState.newFromXY(1, 4), MazeState.newFromXY(2,4), MazeState.newFromXY(3, 4));

    public static final List<MazeState> STATES_MAZE_MIDDLE = List.of(
            MazeState.newFromXY(0, 3), MazeState.newFromXY(1, 3), MazeState.newFromXY(2, 3), MazeState.newFromXY(3, 3));
    public static final List<MazeState> STATES_MAZE_BOTTOM = List.of(
            MazeState.newFromXY(0, 0), MazeState.newFromXY(1, 0), MazeState.newFromXY(2, 0), MazeState.newFromXY(3, 0));
    public static final List<MazeState> STATES_MAZE_MERGED =
            ListUtils.merge(ListUtils.merge(STATES_MAZE_UPPER, STATES_MAZE_MIDDLE), STATES_MAZE_BOTTOM);


    public ForkAndMazeHelper(AgentInterface<S> agentNeural, EnvironmentInterface<S> environment) {
        this.agent = agentNeural;
        this.environment=environment;
    }

    public  void printStateValues() {
        printStateValues(environment.stateSet());
    }

    public  void printStateValues(Set<StateInterface<S>> stateSet) {
        Map<StateInterface<S>, Double> stateValues = getStateValueMap(stateSet);

        DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US)); //US <=> only dots
        stateValues.forEach((s,v) -> System.out.println("s="+s+", v="+formatter.format(v)));
    }

    public  Map<StateInterface<S>, Double> getStateValueMap(Set<StateInterface<S>> stateSet) {
        Map<StateInterface<S>,Double> stateValues=new HashMap<>();
        for (StateInterface<S> state: stateSet) {
              stateValues.put(state, agent.readValue(state));
        }
        return stateValues;
    }





    static BiFunction<Map<StateInterface<ForkVariables>, Double>,Integer,Double> getPos=(m, p) -> m.get(ForkState.newFromPos(p));

    public static double avgErrorFork(Map<StateInterface<ForkVariables>, Double> valueMap) {
        List<Double> errors=new ArrayList<>();
        List<Integer> willLeadToHell=List.of(6,11,12,13,14);
        List<Integer> shallLeadToHeaven=List.of(0,1,2,3,4,5,7,8,9);

        willLeadToHell.forEach(pos -> errors.add(getPos.apply(valueMap,pos)- ForkEnvironment.envSettings.rewardHell()));
        shallLeadToHeaven.forEach(pos -> errors.add(getPos.apply(valueMap,pos)- ForkEnvironment.envSettings.rewardHeaven()));

        return ListUtils.findAverageOfAbsolute(errors).orElseThrow();
    }

    public static double avgErrorMaze(Map<StateInterface<MazeVariables>, Double> valueMap, List<MazeState> states) {
        List<Double> errors=new ArrayList<>();

        Map<StateInterface<MazeVariables>, Double> valueMapCorrect=new HashMap<>();
        double rg = MazeEnvironment.settings.rewardGoal();
        double rm = MazeEnvironment.settings.rewardMove();

        valueMapCorrect.put(MazeState.newFromXY(4, 5),rg+0*rm);
        valueMapCorrect.put(MazeState.newFromXY(3, 5),rg+rm);
        valueMapCorrect.put(MazeState.newFromXY(2, 5),rg+2*rm);
        valueMapCorrect.put(MazeState.newFromXY(1, 5),rg+3*rm);
        valueMapCorrect.put(MazeState.newFromXY(0, 5),rg+4*rm);

        valueMapCorrect.put(MazeState.newFromXY(0, 3),rg+6*rm);
        valueMapCorrect.put(MazeState.newFromXY(1, 3), AgentMazeTabularSettings.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(2, 3), AgentMazeTabularSettings.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(3, 3), AgentMazeTabularSettings.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(4, 3), AgentMazeTabularSettings.VALUE_IF_NOT_PRESENT);

        valueMapCorrect.put(MazeState.newFromXY(5, 3),rg+2*rm);

        valueMapCorrect.put(MazeState.newFromXY(4, 0),rg+5*rm);
        valueMapCorrect.put(MazeState.newFromXY(3, 0),rg+6*rm);
        valueMapCorrect.put(MazeState.newFromXY(2, 0),rg+7*rm);
        valueMapCorrect.put(MazeState.newFromXY(1, 0),rg+8*rm);
        valueMapCorrect.put(MazeState.newFromXY(0, 0),rg+9*rm);


        for (MazeState state:states) {
            errors.add(valueMapCorrect.get(state)-valueMap.get(state));
        }

        System.out.println("errors = " + errors);

        return ListUtils.findAverageOfAbsolute(errors).orElseThrow();
    }

    public static void printStateValuesMaze(List<MazeState> states, Map<StateInterface<MazeVariables>, Double> stateMap ) {
        for (MazeState state:states) {
            System.out.println("State = "+state+", value = "+stateMap.get(state));
        }
    }


}