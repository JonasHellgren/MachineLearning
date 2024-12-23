package book_rl_explained.lunar_lander.domain.agent;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import book_rl_explained.lunar_lander.helpers.RbfMemoryFactory;
import book_rl_explained.radial_basis.RbfNetwork;
import book_rl_explained.radial_basis.TrainData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ActorMemoryLunar {
    RbfNetwork memoryMean;
    RbfNetwork memoryLogStd;
    AgentParameters agentParameters;

    public static ActorMemoryLunar create(AgentParameters p, LunarProperties ep) {
        var memExp = RbfMemoryFactory.createMemoryManyCenters(p, ep, p.learningRateActor());
        var memStd = RbfMemoryFactory.createMemoryOneWideCenter(p, ep, p.learningRateActor());
        return new ActorMemoryLunar(memExp, memStd, p);
    }

    /**
     * Updates the actor's memory based on the provided state, advantage, and gradient.
     *
     * @param state the input state
     * @param adv   the advantage value
     * @param grad  the gradient value, using logStd
     */

    public void fitOne(StateLunar state, double adv, GradientMeanStd grad) {
        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        grad = grad.clip(agentParameters.gradMeanMax(), agentParameters.gradStdMax());
        dataMean.addInAndError(state.asList(), grad.mean() * adv);
        dataStd.addInAndError(state.asList(), grad.std() * adv);
        memoryMean.fit(dataMean);
        memoryLogStd.fit(dataStd);
    }

/*
    public void fitOne0(StateLunar state, double adv, GradientMeanStd grad) {
        var inputs = List.of(state.asList());
        grad=grad.clip(agentParameters.gradMeanMax(), agentParameters.gradStdMax());
        var errorListMean = List.of(grad.mean() * adv);
        var errorListStd = List.of(grad.std() * adv);
        memoryMean.fitFromErrors(inputs, errorListMean);
        memoryLogStd.fitFromErrors(inputs, errorListStd);
    }
*/


    public void fitBatch(TrainData dataMean, TrainData dataStd) {
/*        var dataMean = TrainData.emptyFromErrors();
        var dataStd = TrainData.emptyFromErrors();
        for (int i = 0; i < states.size(); i++) {
            var grad0 = grads0.get(i);
            var grad = grad0.clip(agentParameters.gradMeanMax(), agentParameters.gradStdMax());
            var stateAsList = states.get(i).asList();
            dataMean.addInAndError(stateAsList, grad.mean() * advs.get(i));
            dataStd.addInAndError(stateAsList, grad.std() * advs.get(i));
        }
        int batchSize = Math.min(states.size(), agentParameters.batchSize());*/
/*
        for (int i = 0; i < agentParameters.nEpochs() ; i++) {
        memoryMean.fit(dataMean.createBatch(batchSize));
        memoryLogStd.fit(dataStd.createBatch(batchSize));
        }
*/

        int batchSize = Math.min(dataMean.nSamples(), agentParameters.batchSize());
        memoryMean.fit(dataMean, agentParameters.nEpochs(),batchSize);
        memoryLogStd.fit(dataStd, agentParameters.nEpochs(),batchSize);


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
