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
import com.google.common.collect.Range;
import lombok.extern.java.Log;

@Log
public class RunnerTrainerLunar {

    public static final PlotterAgent.Settings SETTINGS_PLOTTERAGENT = PlotterAgent.Settings.builder()
            .nY(5).nSpd(5).nDigits(1).build();

    public static void main(String[] args) {

        var ep = LunarProperties.defaultProps().withDt(0.5).withRewardFail(-100).withYMax(5).withSpdMax(2);
        var p = AgentParameters.defaultProps(ep)
                .withLearningRateActor(1e-2).withLearningRateCritic(1e-1)
                //.withLearningRateActor(1e-3).withLearningRateCritic(1e-2)
                .withGradStdMax(1e-2).withRangeLogStd(Range.closed(1e-5,5d))
                //.withGradStdMax(1e-3).withRangeLogStd(Range.closed(1e-5,3d))
                .withInitWeightLogStd(-0.5);
        var tp = TrainerParameters.defaultParams()
                .withNEpisodes(5000).withNofStepsMax(30).withGamma(0.98).withTdMax(10);
        var trainerDependencies = TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.of(ep))
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierAllRandom.create(ep))
                .build();

        var trainer= TrainerLunar.of(trainerDependencies);

        trainer.train();

        var progressPlotter= PlotterProgressMeasures.of(trainer);
        progressPlotter.plot("TrainerLunar");

      //  var agentPlotter= PlotterAgent.of(trainerDependencies, SETTINGS_PLOTTERAGENT);
        var agentPlotter= PlotterAgent.ofAgentParameters(trainerDependencies);
        agentPlotter.plotAll();

        trainerDependencies=trainerDependencies.withStartStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep));
        AgentEvaluator evaluator = new AgentEvaluator(trainerDependencies);
        double fractionFails= evaluator.fractionFails(10);
        System.out.println("fractionFails = " + fractionFails);
        evaluator.plotSimulation();


    }
}
