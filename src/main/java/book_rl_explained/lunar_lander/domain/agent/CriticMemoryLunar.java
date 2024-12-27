package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.RbfMemoryFactory;
import book_rl_explained.radial_basis.RbfNetwork;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CriticMemoryLunar {

    RbfNetwork memory;
    AgentParameters agentParameters;

    public static CriticMemoryLunar zeroWeights(AgentParameters p, LunarProperties ep) {
        var mem = RbfMemoryFactory.createMemoryManyCenters(p,ep, p.learningRateCritic());
        return new CriticMemoryLunar(mem,p);
    }

    public double read(StateLunar state) {
        return memory.outPut(state.asList());
    }

    public void fit(TrainData data) {
        int batchSize = Math.min(data.nSamples(), agentParameters.batchSize());
        memory.fit(data, agentParameters.nEpochs(),batchSize);
    }

}
