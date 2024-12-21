package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.RbfMemoryFactory;
import book_rl_explained.radial_basis.RbfNetwork;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class ActorMemoryLunar {
    RbfNetwork memoryMean;
    RbfNetwork memoryLogStd;
    AgentParameters agentParameters;

    public static ActorMemoryLunar create(AgentParameters p, LunarProperties ep) {
        var memExp = RbfMemoryFactory.createMemoryManyCenters(p,ep,p.learningRateActor());
        var memStd = RbfMemoryFactory.createMemoryOneWideCenter(p,ep,p.learningRateActor());
        return new ActorMemoryLunar(memExp, memStd, p);
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
        memoryMean.fitFromErrors(inputs, errorListMean);
        memoryLogStd.fitFromErrors(inputs, errorListStd);
    }

    public void fitBatch(List<StateLunar> states, List<Double> advs, List<GradientMeanStd> grads) {
        var inputs = states.stream().map(StateLunar::asList).toList();

        List<Double> errorListMean=new ArrayList<>();
        for (int i = 0; i < advs.size() ; i++) {
            errorListMean.add( grads.get(i).mean()*advs.get(i));
        }
        List<Double> errorListStd=new ArrayList<>();
        for (int i = 0; i < advs.size() ; i++) {
            errorListStd.add( grads.get(i).std()*advs.get(i));
        }
        memoryMean.fitFromErrors(inputs, errorListMean);
        memoryLogStd.fitFromErrors(inputs, errorListStd);
    }

    /**
     * Returns the mean and standard deviation of the actor's output for the given state.
     *
     * @param state the input state
     * @return a MeanAndStd object containing the mean and standard deviation of the actor's output
     */

    public MeanStd actorMeanAndStd(StateLunar state) {
        var in = state.asList();
        return MeanStd.of(memoryMean.outPut(in), Math.exp(memoryLogStd.outPut(in)));
    }

    public MeanStd actorMeanAndLogStd(StateLunar state) {
        var in = state.asList();
        return MeanStd.of(memoryMean.outPut(in), memoryLogStd.outPut(in));
    }


}
