package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.trainer.MultiStepResults;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;

/***
 * The product of the gradient of the PDF and the advantage function is often clipped to prevent exploding gradients
  . Clipping can be done in two ways:
 1. Clipping the product: Clip the product of the gradient of the PDF and the advantage function together.
 2. Clipping the parts: Clip the gradient of the PDF and the advantage function separately before multiplying them.

  Clipping the parts (i.e., clipping the gradient of the PDF and the advantage function separately) is generally
  considered a better approach. Here's why:

  Clipping the product can lead to biased gradients, as the clipping operation can affect the direction of the gradient.
  Clipping the parts separately helps to prevent exploding gradients while preserving the direction of the gradient.
  In particular, clipping the advantage function can help to reduce the variance of the policy gradient estimate, while
  clipping the gradient of the PDF can help to prevent exploding gradients due to large policy updates.
 */

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
            agent.fitCritic(data);
            agent.copyActivationsInCriticToActorMean();
            agent.fitActorUseCriticActivations(dataMean, dataStd);
        }
    }


}
