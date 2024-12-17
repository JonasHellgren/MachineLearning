package book_rl_explained.lunar_lander.runners;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierAllRandom;
import book_rl_explained.lunar_lander.domain.trainer.*;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;

public class RunnerTrainerLunarMultiStep {

    public static void main(String[] args) {
        var ep = getEnvironmentProperties();
        var trainerDependencies = getDependencies(ep);
        var trainer= TrainerLunarMultiStep.of(trainerDependencies);
        trainer.train();
        plot(trainer, trainerDependencies);
        RunnerHelper.evaluate(trainerDependencies, ep);
    }

    private static LunarProperties getEnvironmentProperties() {
        return LunarProperties.defaultProps(); //.withSpdMax(3);
    }

    private static TrainerDependencies getDependencies(LunarProperties ep) {
        var p = AgentParameters.defaultParams(ep);
        var tp = TrainerParameters.newDefault().withNEpisodes(5000).withGamma(0.99).withStepHorizon(3);
        return TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierAllRandom.create(ep))
                .build();
    }

    public static final PlotterAgent.Settings SETTINGS_PLOTTER_AGENT = PlotterAgent.Settings.builder()
            .nY(6).nSpd(7).nDigitsAxisLabels(1).valueFormat("%.1f").build();

    private static void plot(TrainerI trainer, TrainerDependencies trainerDependencies) {
        var progressPlotter= PlotterProgressMeasures.of(trainer);
        progressPlotter.plot("TrainerLunar");
        var agentPlotter= PlotterAgent.of(trainerDependencies, SETTINGS_PLOTTER_AGENT);
        agentPlotter.plotAll();
    }

}
