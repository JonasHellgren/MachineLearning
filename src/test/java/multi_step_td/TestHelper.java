package multi_step_td;

import common.ListUtils;
import multi_step_temp_diff.agents.AgentMazeTabular;
import multi_step_temp_diff.environments.*;
import multi_step_temp_diff.interfaces_and_abstract.EnvironmentInterface;
import multi_step_temp_diff.interfaces_and_abstract.NetworkMemoryInterface;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import org.junit.Assert;

import java.util.*;
import java.util.function.BiFunction;

public class TestHelper<S> {

    NetworkMemoryInterface<S> memoryNeural;  //todo use AgentInfo instead
    EnvironmentInterface<S> environment;

    public TestHelper(NetworkMemoryInterface<S> memory, EnvironmentInterface<S> environment) {
        this.memoryNeural = memory;
        this.environment=environment;
    }

    public  void printStateValues() {
        Map<StateInterface<S>,Double> stateValues=new HashMap<>();
        for (StateInterface<S> state: environment.stateSet()) {
            stateValues.put(state, memoryNeural.read(state));
        }
        stateValues.forEach((s,v) -> System.out.println("s="+s+", v="+v));
    }

    public void assertAllStates(double value, double delta) {
        //for (int si = 0; si < ForkEnvironment.NOF_STATES ; si++) {
        for (StateInterface<S> state:environment.stateSet()) {
            Assert.assertEquals(value, memoryNeural.read(state), delta);
        }
    }

    static BiFunction<Map<StateInterface<ForkVariables>, Double>,Integer,Double> getPos=(m, p) -> m.get(ForkState.newFromPos(p));

    public static double avgErrorFork(Map<StateInterface<ForkVariables>, Double> valueMap) {
        List<Double> errors=new ArrayList<>();
        errors.add(Math.abs(getPos.apply(valueMap,0)-ForkEnvironment.R_HEAVEN));
        errors.add(Math.abs(getPos.apply(valueMap,7)-ForkEnvironment.R_HEAVEN));
        errors.add(Math.abs(getPos.apply(valueMap,6)-ForkEnvironment.R_HELL));
        errors.add(Math.abs(getPos.apply(valueMap,11)-ForkEnvironment.R_HELL));
        return ListUtils.findAverageOfAbsolute(errors).orElseThrow();
    }

    public static double avgErrorMaze(Map<StateInterface<MazeVariables>, Double> valueMap,List<MazeState> states) {
        List<Double> errors=new ArrayList<>();

        Map<StateInterface<MazeVariables>, Double> valueMapCorrect=new HashMap<>();
        double rg = MazeEnvironment.REWARD_GOAL;
        double rm = MazeEnvironment.REWARD_MOVE;

        valueMapCorrect.put(MazeState.newFromXY(3, 5),rg+rm);
        valueMapCorrect.put(MazeState.newFromXY(2, 5),rg+2*rm);
        valueMapCorrect.put(MazeState.newFromXY(1, 5),rg+3*rm);
        valueMapCorrect.put(MazeState.newFromXY(0, 5),rg+4*rm);

        valueMapCorrect.put(MazeState.newFromXY(0, 3),rg+6*rm);
        valueMapCorrect.put(MazeState.newFromXY(1, 3), AgentMazeTabular.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(2, 3), AgentMazeTabular.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(3, 3), AgentMazeTabular.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(4, 3), AgentMazeTabular.VALUE_IF_NOT_PRESENT);
        valueMapCorrect.put(MazeState.newFromXY(5, 3),rg+2*rm);

        valueMapCorrect.put(MazeState.newFromXY(4, 0),rg+5*rm);
        valueMapCorrect.put(MazeState.newFromXY(3, 0),rg+6*rm);
        valueMapCorrect.put(MazeState.newFromXY(2, 0),rg+7*rm);
        valueMapCorrect.put(MazeState.newFromXY(1, 0),rg+8*rm);
        valueMapCorrect.put(MazeState.newFromXY(0, 0),rg+9*rm);


        for (MazeState state:states) {
            errors.add(valueMapCorrect.get(state)-valueMap.get(state));
        }

        return ListUtils.findAverageOfAbsolute(errors).orElseThrow();
    }



}
