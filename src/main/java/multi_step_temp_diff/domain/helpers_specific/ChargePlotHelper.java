package multi_step_temp_diff.domain.helpers_specific;

import common.MathUtils;
import common.MovingAverage;
import lombok.AllArgsConstructor;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.helpers_common.ValueTracker;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_common.AgentInfo;
import multi_step_temp_diff.domain.trainer.NStepNeuralAgentTrainer;
import org.apache.commons.lang3.tuple.Pair;
import plotters.PlotterMultiplePanelsPairs;
import plotters.PlotterMultiplePanelsTrajectory;
import plotters.PlotterScatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ChargePlotHelper {

    public  final int LENGTH_FILTER_WINDOW = 500;
    public  final int TRAP_POS = 29;
    public  final int MAX_TD_ERROR_IN_PLOT = 2;

    AgentNeuralInterface<ChargeVariables> agent;
    NStepNeuralAgentTrainer<ChargeVariables> trainer;

    public  void plotV20MinusV11VersusSoC(int posB, double socB) {
        List<List<Pair<Double, Double>>> listOfPairs = new ArrayList<>();
        List<Pair<Double, Double>> valueDiffVsSoC = new ArrayList<>();
        for (int socInt = 20; socInt < 100; socInt++) {
            double socA = (double) socInt / 100d;
            ChargeState state20 = new ChargeState(ChargeVariables.builder().posA(20).posB(posB).socA(socA).socB(socB).build());
            ChargeState state11 = new ChargeState(ChargeVariables.builder().posA(11).posB(posB).socA(socA).socB(socB).build());
            valueDiffVsSoC.add(Pair.of(socA, agent.readValue(state20) - agent.readValue(state11)));
        }
        PlotterMultiplePanelsPairs plotter = new PlotterMultiplePanelsPairs("soc", "v20-v11");
        listOfPairs.add(valueDiffVsSoC);
        plotter.plot(listOfPairs);
    }

    public  void createScatterPlot(ChargeEnvironmentSettings envSettings, String xAxisTitle, double socA, int trapPos) {
        var plotter = new PlotterScatter(xAxisTitle, "Pos");
        List<Pair<Double, Double>> dataPairs = new ArrayList<>();
        for (int pos : envSettings.siteNodes()) {
            ChargeState state = new ChargeState(ChargeVariables.builder().posA(pos).posB(trapPos).socA(socA).build());
            double value = agent.readValue(state);
            dataPairs.add(Pair.of((double) pos, value));
        }
        plotter.plot(dataPairs);
    }

    public  void plotTdError() {
        AgentInfo<ChargeVariables> agentInfo = new AgentInfo<>(agent);
        List<Double> filtered1 = agentInfo.getFilteredTemporalDifferenceList(LENGTH_FILTER_WINDOW);
        List<Double> filteredAndClipped = filtered1.stream().map(n -> MathUtils.clip(n, 0, MAX_TD_ERROR_IN_PLOT)).toList();
        plotTrajectory(filteredAndClipped, "Step", "TD error");

    }

    public  void plotSumRewardsTracker() {
        List<Double> trajectory = trainer.getHelper().getSumRewardsTracker().getValueHistory();
        plotTrajectory(trajectory, "Episode", "sumRewards");
    }


    public  void plotTrajectory(List<Double> trajectory, String xLabel, String yLabel) {
        List<List<Double>> listOfTrajectories = new ArrayList<>();
        listOfTrajectories.add(trajectory);
        PlotterMultiplePanelsTrajectory plotter = new PlotterMultiplePanelsTrajectory(Collections.singletonList(yLabel), xLabel);
        plotter.plot(listOfTrajectories);
    }

    public static final String PICS_FOLDER = "pics/";
    public void plotAndSaveErrorHistory(String fileName) {
        AgentChargeNeural agentCasted = (AgentChargeNeural) agent;
        ValueTracker errorTracker=agentCasted.getErrorHistory();
        PlotterMultiplePanelsTrajectory plotter=new PlotterMultiplePanelsTrajectory(List.of("Error "+fileName),"iter");
        MovingAverage movingAverage=new MovingAverage(
                LENGTH_FILTER_WINDOW,errorTracker.getValueHistoryAbsoluteValues());
        plotter.plot(List.of(movingAverage.getFiltered()));
        //Thread.sleep(100);
        plotter.saveImage(PICS_FOLDER +fileName);
    }

}
