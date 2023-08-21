package multi_step_temp_diff.domain.helpers_specific;

import common.ListUtils;
import common.MathUtils;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.agents.fork.AgentForkNeural;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ForkEnvironmentSettings;
import multi_step_temp_diff.domain.environments.fork.ForkEnvironment;
import multi_step_temp_diff.domain.environments.fork.ForkState;
import multi_step_temp_diff.domain.environments.fork.ForkVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import plotters.PlotterMultiplePanelsTrajectory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ForkHelper {

    private static final int LENGTH_WINDOW = 1000;
    public static final int MAX_VALUE_IN_PLOT = 5;

    EnvironmentInterface<ForkVariables> environment;

    public ForkHelper(EnvironmentInterface<ForkVariables> environment) {
        this.environment = environment;
    }

    public void plotTdError(AgentNeuralInterface<ForkVariables> agent, String yTitle) {
        AgentInfo<ForkVariables> agentInfo = new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories = new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_WINDOW);
        List<Double> filteredAndClipped = filtered1.stream()
                .mapToDouble(n -> MathUtils.clip(n, 0, MAX_VALUE_IN_PLOT)).boxed().toList();
        listOfTrajectories.add(filteredAndClipped);
        PlotterMultiplePanelsTrajectory plotter =
                new PlotterMultiplePanelsTrajectory(Collections.singletonList(yTitle), "Step");
        plotter.plot(listOfTrajectories);
    }


    static BiFunction<Map<StateInterface<ForkVariables>, Double>,Integer,Double> getPos=(m, p) -> m.get(ForkState.newFromPos(p));


    public  double avgErrorFork(Map<StateInterface<ForkVariables>, Double> valueMap) {
        List<Double> errors=new ArrayList<>();
        List<Integer> willLeadToHell=List.of(6,11,12,13,14);
        List<Integer> shallLeadToHeaven=List.of(0,1,2,3,4,5,7,8,9);

        ForkEnvironment envCasted=(ForkEnvironment) environment;
        ForkEnvironmentSettings envSettings=envCasted.envSettings;

        willLeadToHell.forEach(pos -> errors.add(getPos.apply(valueMap,pos)- envSettings.rewardHell()));
        shallLeadToHeaven.forEach(pos -> errors.add(getPos.apply(valueMap,pos)- envSettings.rewardHeaven()));

        return ListUtils.findAverageOfAbsolute(errors).orElseThrow();
    }

}
