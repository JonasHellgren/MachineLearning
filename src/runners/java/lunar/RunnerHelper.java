package lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierFromMaxHeight;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomAndClipped;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.TrainerI;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import book_rl_explained.lunar_lander.helpers.AgentEvaluator;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.util.Pair;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RunnerHelper {

    public static final double MIN_START_HEIGHT = 3d;
    public static final double MIN_START_SPD = -2d;
    public static final double MAX_START_SPD = -0d;
    public static final String CHART_DIR = "src/runners/java/lunar/pics/";

    public static void evaluate(TrainerDependencies trainerDependencies, LunarProperties ep) {
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep));
        var evaluatorFails = AgentEvaluator.of(trainerDependencies);
        double fractionFails= evaluatorFails.fractionFails(10);
        System.out.println("fractionFails = " + fractionFails);
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierFromMaxHeight.create(ep));
        var evaluatorSim = AgentEvaluator.of(trainerDependencies);
        evaluatorSim.plotAndSavePicFromSimulation(CHART_DIR);
    }


    static TrainerDependencies getDependencies(LunarProperties ep, int stepHorizon, int nEpisodes) {
        var p = AgentParameters.newDefault(ep);
        var tp = TrainerParameters.newDefault().withStepHorizon(stepHorizon).withNEpisodes(nEpisodes);
        return TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierRandomAndClipped.create(
                        ep, Pair.create(MIN_START_HEIGHT, ep.yMax()), Pair.create(MIN_START_SPD, MAX_START_SPD)))
                .build();
    }

    public static final PlotterAgent.Settings SETTINGS_PLOTTER_AGENT = PlotterAgent.Settings.builder()
            .nY(50).nSpd(50).nDigitsAxisLabels(1).valueFormat("%.1f").build();

    static void plot(TrainerI trainer, TrainerDependencies trainerDependencies) {
        var progressPlotter = PlotterProgressMeasures.of(trainer);
        progressPlotter.plot();
        var agentPlotter = PlotterAgent.of(trainerDependencies, SETTINGS_PLOTTER_AGENT);
        agentPlotter.plotAndSaveAll();
    }


}
