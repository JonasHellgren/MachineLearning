package lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierAllRandom;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import book_rl_explained.lunar_lander.domain.trainer.*;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;
import lombok.extern.java.Log;
import org.hellgren.utilities.timer.CpuTimer;

@Log
public class RunnerTrainerLunarMultiStep {

     static final boolean IS_BATCH_LEARNING = false;

    public static void main(String[] args) {
        var ep =  LunarProperties.defaultProps();
        var trainerDependencies = getDependencies(ep);
        var trainer = TrainerLunarMultiStep.of(trainerDependencies);
        trainer.train();
        plot(trainer, trainerDependencies);
        RunnerHelper.evaluate(trainerDependencies, ep);
    }

    private static TrainerDependencies getDependencies(LunarProperties ep) {

        var p = IS_BATCH_LEARNING ?
                AgentParameters.defaultParams(ep).withLearningRateCritic(0.9).withLearningRateActor(0.1)
                        .withBatchSize(10).withNEpochs(1)
                : AgentParameters.defaultParams(ep).withLearningRateCritic(0.01).withLearningRateActor(0.001);
        var tp = TrainerParameters.newDefault()
                .withStepHorizon(5).withNEpisodes(5_000).withBatchLearning(IS_BATCH_LEARNING);
        return TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                //.startStateSupplier(StartStateSupplierAllRandom.create(ep))
                .startStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep))
                .build();
    }

    public static final PlotterAgent.Settings SETTINGS_PLOTTER_AGENT = PlotterAgent.Settings.builder()
            .nY(50).nSpd(50).nDigitsAxisLabels(1).valueFormat("%.1f").build();

    private static void plot(TrainerI trainer, TrainerDependencies trainerDependencies) {
        var progressPlotter = PlotterProgressMeasures.of(trainer);
        progressPlotter.plot();
        var agentPlotter = PlotterAgent.of(trainerDependencies, SETTINGS_PLOTTER_AGENT);
        agentPlotter.plotAll();
    }

}
