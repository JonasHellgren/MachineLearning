package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.ValueCalculator;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class MemoryUpdaterSingleStepTrainer {

    TrainerDependencies dependencies;
    ValueCalculator calculator;


    public static MemoryUpdaterSingleStepTrainer of(TrainerDependencies dependencies) {
        return new MemoryUpdaterSingleStepTrainer(dependencies,ValueCalculator.of(dependencies));
    }

    public void fit(List<ExperienceLunar> experiences) {
        var agent = dependencies.agent();
        var tp = dependencies.trainerParameters();
        var ap = agent.getAgentParameters();
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        var data = TrainData.emptyFromErrors();
        for (ExperienceLunar experience : experiences) {
            double tdError = ap.clipTdError(calculator.temporalDifferenceError(experience));
            var state = experience.state();
            var grad = agent.gradientMeanAndLogStd(state, experience.action());
            grad = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            dataMean.clear();
            dataStd.clear();
            data.clear();
            dataMean.addInAndError(state.asList(), grad.mean() * tdError);
            dataStd.addInAndError(state.asList(), grad.std() * tdError);
            data.addInAndError(state.asList(), tdError);
            agent.fitActorBatch(dataMean, dataStd);
            agent.fitCriticBatch(data);
        }
    }

}
