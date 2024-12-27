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
     * Fits the actor's memory to the provided training data.
     *
     * This method updates the mean and standard deviation of the actor's output
     * based on the provided training data.
     *
     * @param dataMean the training data for the mean of the actor's output
     * @param dataStd the training data for the standard deviation of the actor's output
     */

    public void fit(TrainData dataMean, TrainData dataStd) {
        int batchSize = Math.min(dataMean.nSamples(), agentParameters.batchSize());
        memoryMean.fit(dataMean, agentParameters.nEpochs(),batchSize);
        memoryLogStd.fit(dataStd, agentParameters.nEpochs(),batchSize);
    }


    public void fitNoUpdateActivationsMean(TrainData dataMean, TrainData dataStd) {
        int batchSize = Math.min(dataMean.nSamples(), agentParameters.batchSize());
        memoryMean.fitNoUpdateActivations(dataMean, agentParameters.nEpochs(),batchSize);
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


    public void copyActivationsInCriticToActorMean(CriticMemoryLunar criticMemory) {
        memoryMean.copyActivations(criticMemory.memory);
    }
}
