package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.MemoryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;
import java.util.List;

@AllArgsConstructor
@Getter
public class ActorMemoryLunar {

    RadialBasis memoryMean;
    RadialBasis memoryLogStd;
    WeightUpdater updaterMean;
    WeightUpdater updaterLogStd;
    AgentParameters agentParameters;

    public static ActorMemoryLunar create(AgentParameters p, LunarProperties ep) {
        var memExp = MemoryFactory.createMemoryManyCenters(p,ep);
        var memStd = MemoryFactory.createMemoryOneWideCenter(p,ep);
        var updExp = new WeightUpdater(memExp,p.learningRateActor());
        var updStd = new WeightUpdater(memStd,p.learningRateActor());
        return new ActorMemoryLunar(memExp, memStd, updExp, updStd,p);
    }

    /**
     * Updates the actor's memory based on the provided state, advantage, and gradient.
     *
     * @param state the input state
     * @param adv the advantage value
     * @param grad the gradient value, using logStd
     */

    public void fit(StateLunar state, double adv, GradientMeanStd grad) {
        var inputs = List.of(state.asList());
        grad=grad.clip(agentParameters.gradMeanMax(), agentParameters.gradStdMax());
        var errorListMean = List.of(grad.mean() * adv);
        var errorListStd = List.of(grad.std() * adv);
        updaterMean.updateWeightsFromErrors(inputs, errorListMean);
        updaterLogStd.updateWeightsFromErrors(inputs, errorListStd);
    }

    /**
     * Returns the mean and standard deviation of the actor's output for the given state.
     *
     * @param state the input state
     * @return a MeanAndStd object containing the mean and standard deviation of the actor's output
     */

    public MeanAndStd actorMeanAndStd(StateLunar state) {
        var in = state.asList();
        return MeanAndStd.of(memoryMean.outPut(in), Math.exp(memoryLogStd.outPut(in)));
    }

    public MeanAndStd actorMeanAndLogStd(StateLunar state) {
        var in = state.asList();
        return MeanAndStd.of(memoryMean.outPut(in), memoryLogStd.outPut(in));
    }

}
