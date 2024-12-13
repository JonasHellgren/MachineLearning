package book_rl_explained.lunar_lander.runners;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierAllRandom;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierFromMaxHeight;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.TrainerLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import book_rl_explained.lunar_lander.helpers.AgentEvaluator;
import book_rl_explained.lunar_lander.helpers.PlotterAgent;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import lombok.extern.java.Log;

@Log
public class RunnerTrainerLunar {


    public static void main(String[] args) {
        var ep = getEnvironmentProperties();
        var trainerDependencies = getDependencies(ep);
        var trainer= TrainerLunar.of(trainerDependencies);
        trainer.train();
        plot(trainer, trainerDependencies);
        evaluate(trainerDependencies, ep);
    }

    private static LunarProperties getEnvironmentProperties() {
        return LunarProperties.defaultProps(); //.withSpdMax(3);
    }

    private static TrainerDependencies getDependencies(LunarProperties ep) {
        var p = AgentParameters.defaultParams(ep);
        var tp = TrainerParameters.defaultParams().withNEpisodes(50000).withGamma(0.99);
        return TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierAllRandom.create(ep))
                .build();
    }

    public static final PlotterAgent.Settings SETTINGS_PLOTTERAGENT = PlotterAgent.Settings.builder()
            .nY(6).nSpd(7).nDigitsAxisLabels(1).valueFormat("%.1f").build();

    private static void plot(TrainerLunar trainer, TrainerDependencies trainerDependencies) {
        var progressPlotter= PlotterProgressMeasures.of(trainer);
        progressPlotter.plot("TrainerLunar");
        var agentPlotter= PlotterAgent.of(trainerDependencies, SETTINGS_PLOTTERAGENT);
        agentPlotter.plotAll();
    }


    private static void evaluate(TrainerDependencies trainerDependencies, LunarProperties ep) {
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep));
        var evaluatorFails = AgentEvaluator.of(trainerDependencies);
        double fractionFails= evaluatorFails.fractionFails(10);
        System.out.println("fractionFails = " + fractionFails);
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierFromMaxHeight.create(ep));
        var evaluatorSim = AgentEvaluator.of(trainerDependencies);
        evaluatorSim.plotSimulation();
    }

}
