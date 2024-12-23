package lunar;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierAllRandom;
import book_rl_explained.lunar_lander.domain.trainer.*;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;

public class RunnerTrainerLunarMultiStep {

     static final boolean IS_BATCH_LEARNING = true;

    public static void main(String[] args) {
        var ep = getEnvironmentProperties();
        var trainerDependencies = getDependencies(ep);
        var trainer = TrainerLunarMultiStep.of(trainerDependencies);
        trainer.train();
        plot(trainer, trainerDependencies);
        RunnerHelper.evaluate(trainerDependencies, ep);
    }

    private static LunarProperties getEnvironmentProperties() {
        return LunarProperties.defaultProps();
    }

    private static TrainerDependencies getDependencies(LunarProperties ep) {


        var p = IS_BATCH_LEARNING ?
                AgentParameters.defaultParams(ep).withLearningRateCritic(0.5).withLearningRateActor(0.05)
                        .withBatchSize(20).withNEpochs(1)
                : AgentParameters.defaultParams(ep).withLearningRateCritic(0.01).withLearningRateActor(0.001);
        var tp = TrainerParameters.newDefault()
                .withStepHorizon(5).withNEpisodes(3000).withBatchLearning(IS_BATCH_LEARNING);
        return TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierAllRandom.create(ep))
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
