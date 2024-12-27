package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.MultiStepResults;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MemoryUpdaterMultiStepTrainer {

    TrainerDependencies dependencies;

    public static MemoryUpdaterMultiStepTrainer of(TrainerDependencies dependencies) {
       return new MemoryUpdaterMultiStepTrainer(dependencies);
    }

    public void fit(MultiStepResults msr) {
        var agent = dependencies.agent();
        var ap = agent.getAgentParameters();
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        var data = TrainData.emptyFromErrors();

        for (int step = 0; step < msr.nExperiences(); step++) {
            double adv = ap.clipAdvantage(msr.advantageAtStep(step));
            var state = msr.stateAtStep(step);
            var grad = agent.gradientMeanAndLogStd(state, msr.actionAtStep(step));
            grad = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            dataMean.clear();
            dataStd.clear();
            data.clear();
            dataMean.addInAndError(state.asList(), grad.mean() * adv);
            dataStd.addInAndError(state.asList(), grad.std() * adv);
            data.addInAndError(msr.stateAtStep(step).asList(), adv);
            agent.fitActorBatch(dataMean, dataStd);
            agent.fitCriticBatch(data);
        }
    }

}
