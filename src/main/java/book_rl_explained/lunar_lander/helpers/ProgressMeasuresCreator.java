package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.MultiStepResults;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.ValueCalculator;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ProgressMeasuresCreator {

    TrainerDependencies dependencies;
    ValueCalculator calculator;

    public static ProgressMeasuresCreator of(TrainerDependencies dependencies) {
        return new ProgressMeasuresCreator(dependencies, ValueCalculator.of(dependencies));
    }

    public ProgressMeasures getProgressMeasures(List<ExperienceLunar> experiences, MultiStepResults msrs) {
        var agent = dependencies.agent();
        var ap = agent.getAgentParameters();
        var tp = dependencies.trainerParameters();
        var measureLists = ProgressMeasures.MeasureLists.empty();
        for (ExperienceLunar experience : experiences) {
            double td0 = calculator.calcTemporalDifferenceError(experience);
            double td = ap.clipTdError(td0);
            var grad = agent.gradientMeanAndLogStd(experience.state(), experience.action());
            var gradClipped = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            var mAndStd = agent.readActor(experience.state());
            double tdErrorBestAction = calculator.calcTemporalDifferenceErrorBestAction(experience);
            //double adv=calculator.advantage(agent,experience.state(),tdErrorBestAction);
            measureLists.addTd(Math.abs(td0));
            measureLists.addTdClipped(Math.abs(td));
            measureLists.addStd(mAndStd.std());

            if (msrs == null) {
                measureLists.addGradMean(0);
                measureLists.addGradMeanClipped(0);
            } else {
                double adv = msrs.advantageAtStep(experiences.indexOf(experience));
                measureLists.addGradMean(gradClipped.mean() * adv * 0);
                measureLists.addGradMeanClipped(gradClipped.mean() * adv);
            }
            measureLists.addTdBestAction(Math.abs(tdErrorBestAction));
        }
        var pm = ProgressMeasures.of(experiences, measureLists);
        pm = addValuesSpecifStates(agent, pm);
        return pm;
    }

    private static ProgressMeasures addValuesSpecifStates(AgentI agent, ProgressMeasures pm) {
        double stateValuePos2Spd0 = agent.readCritic(StateLunar.of(2, 0));
        pm = pm.withStateValuePos2Spd0(stateValuePos2Spd0);
        double stateValuePos5Spd2 = agent.readCritic(StateLunar.of(5, 2));
        pm = pm.withStateValuePos5Spd2(stateValuePos5Spd2);
        return pm;
    }


}
