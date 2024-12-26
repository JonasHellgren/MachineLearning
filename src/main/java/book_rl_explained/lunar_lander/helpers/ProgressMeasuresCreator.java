package book_rl_explained.lunar_lander.helpers;

import book_rl_explained.lunar_lander.domain.agent.AgentI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.domain.trainer.ExperienceLunar;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.domain.trainer.ValueCalculator;
import com.beust.jcommander.internal.Lists;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ProgressMeasuresCreator {

    TrainerDependencies dependencies;
    ValueCalculator calculator;

    public static ProgressMeasuresCreator of(TrainerDependencies dependencies) {
        return new ProgressMeasuresCreator(dependencies, ValueCalculator.of(dependencies));
    }

    public ProgressMeasures progressMeasures(List<ExperienceLunar> experiences) {
        var agent = dependencies.agent();
        var ap = agent.getAgentParameters();
        var tp = dependencies.trainerParameters();
        var tdList = Lists.<Double>newArrayList();
        var tdListClipped = Lists.<Double>newArrayList();
        var gradMeanList = Lists.<Double>newArrayList();
        var gradMeanListClipped = Lists.<Double>newArrayList();

        var stdList = Lists.<Double>newArrayList();
        for (ExperienceLunar experience : experiences) {
            double td0 = calculator.temporalDifferenceError(experience);
            double td = tp.clipTdError(td0);
            var grad = agent.gradientMeanAndLogStd(experience.state(), experience.action());
            var gradClipped = grad.clip(ap.gradMeanMax(), ap.gradStdMax());
            var mAndStd = agent.readActor(experience.state());
            tdList.add(Math.abs(td0));
            tdListClipped.add(Math.abs(td));
            stdList.add(mAndStd.std());
            gradMeanList.add(grad.mean());
            gradMeanListClipped.add(gradClipped.mean());
        }
        ProgressMeasures pm = ProgressMeasures.of(experiences, tdList, tdListClipped, stdList, gradMeanList, gradMeanListClipped);
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
