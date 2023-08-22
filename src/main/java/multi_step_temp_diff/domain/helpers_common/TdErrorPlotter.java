package multi_step_temp_diff.domain.helpers_common;

import common.MathUtils;
import lombok.Builder;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import plotters.PlotterMultiplePanelsTrajectory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Builder
public class TdErrorPlotter<S> {

    @Builder.Default
    Double maxValueInPlot=5d;
    @Builder.Default
    int lengthWindow = 1000;
    AgentNeuralInterface<S> agent;
    @Builder.Default
    String yTitle="TD Error";

    public void plotTdError() {
        AgentInfo<S> agentInfo = new AgentInfo<>(agent);
        List<List<Double>> listOfTrajectories = new ArrayList<>();
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(lengthWindow);
        List<Double> filteredAndClipped = filtered1.stream()
                .mapToDouble(n -> MathUtils.clip(n, 0, maxValueInPlot)).boxed().toList();
        listOfTrajectories.add(filteredAndClipped);
        PlotterMultiplePanelsTrajectory plotter =
                new PlotterMultiplePanelsTrajectory(Collections.singletonList(yTitle), "Step");
        plotter.plot(listOfTrajectories);
    }
}
