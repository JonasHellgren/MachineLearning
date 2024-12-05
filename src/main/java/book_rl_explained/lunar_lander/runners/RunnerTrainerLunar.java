package book_rl_explained.lunar_lander.runners;

import book_rl_explained.lunar_lander.domain.agent.AgentLunar;
import book_rl_explained.lunar_lander.domain.agent.AgentParameters;
import book_rl_explained.lunar_lander.domain.environment.EnvironmentLunar;
import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.TrainerLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerParameters;
import book_rl_explained.lunar_lander.helpers.PlotterProgressMeasures;
import book_rl_explained.lunar_lander.helpers.StartStateSupplierRandomHeightZeroSpeed;

public class RunnerTrainerLunar {

    public static void main(String[] args) {

        var ep = LunarProperties.defaultProps();
        var p = AgentParameters.defaultProps(ep).withLearningRateActor(0.01).withLearningRateCritic(0.01);
            //    .withGradStdMax(0.1);
        var tp = TrainerParameters.defaultParams().withNEpisodes(3000).withNofStepsMax(100);
        var trainerDependencies = TrainerDependencies.builder()
                .agent(AgentLunar.zeroWeights(p, ep))
                .environment(EnvironmentLunar.createDefault())
                .trainerParameters(tp)
                .startStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep))
                .build();


        var trainer= TrainerLunar.of(trainerDependencies);

        trainer.train();

        var plotter= PlotterProgressMeasures.of(trainer);
        plotter.plot("TrainerLunar");

    }
}
