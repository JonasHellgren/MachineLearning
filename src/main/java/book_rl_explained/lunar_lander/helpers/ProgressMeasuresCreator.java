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
        var tp = dependencies.trainerParameters();
        var tdList = Lists.<Double>newArrayList();
        var stdList = Lists.<Double>newArrayList();
        for (ExperienceLunar experience : experiences) {
            double e = tp.clipTdError(calculator.temporalDifferenceError(experience));
            tdList.add(Math.abs(e));
            var mAndStd = agent.readActor(experience.state());
            stdList.add(mAndStd.std());
        }
        ProgressMeasures pm = ProgressMeasures.of(experiences, tdList, stdList);
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
