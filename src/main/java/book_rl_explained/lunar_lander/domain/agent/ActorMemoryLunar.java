package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.MemoryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.math3.util.Pair;
import super_vised.radial_basis.RadialBasis;
import super_vised.radial_basis.WeightUpdater;

import java.util.List;

@AllArgsConstructor
@Getter
public class ActorMemoryLunar {

    RadialBasis memoryExp;
    RadialBasis memoryStd;
    WeightUpdater updaterExp;
    WeightUpdater updaterStd;
    AgentParameters agentParameters;

    public static ActorMemoryLunar zeroWeights(AgentParameters p, LunarProperties ep) {
        var memExp = MemoryFactory.createMemoryManyCenters(p,ep);
        var memStd = MemoryFactory.createMemoryOneWideCenter(p,ep);
        var updExp = new WeightUpdater(memExp,p.learningRate());
        var updStd = new WeightUpdater(memStd,p.learningRate());
        return new ActorMemoryLunar(memExp, memStd, updExp, updStd,p);
    }

    /**
     * //todo clip grad
     * @param state
     * @param adv
     * @param grad0
     */

    public void fit(StateLunar state, double adv, MeanAndStd grad0) {
        var inputs = List.of(state.asList());
        var grad=grad0.createClipped(agentParameters);
        var errorListMean = List.of(grad.mean() * adv);
        var errorListStd = List.of(grad.std() * adv);
        updaterExp.updateWeightsFromErrors(inputs, errorListMean);
        updaterStd.updateWeightsFromErrors(inputs, errorListStd);
    }


    public MeanAndStd actorMeanAndStd(StateLunar state) {
        var in = state.asList();
        return MeanAndStd.of(memoryExp.outPut(in),Math.exp(memoryStd.outPut(in)));
    }

}
