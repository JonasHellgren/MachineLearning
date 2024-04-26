package safe_rl.domain.trainers;

import common.linear_regression_batch_fitting.LinearBatchFitter;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;
import safe_rl.agent_interfaces.AgentACDiscoI;
import safe_rl.domain.memories.ReplayBuffer;
import safe_rl.domain.value_classes.Experience;
import safe_rl.domain.value_classes.TrainerParameters;
import safe_rl.environments.trading_electricity.StateTrading;
import safe_rl.environments.trading_electricity.VariablesTrading;
import safe_rl.helpers.EpisodeInfo;

import java.util.List;

public class CriticTrainerUsingReplayBuffer<V> {

    public static final int N_FEAT = 1;
    AgentACDiscoI<V> agent;
    TrainerParameters trainerParameters;

    LinearBatchFitter linearFitter;

    public CriticTrainerUsingReplayBuffer(AgentACDiscoI<V> agent, TrainerParameters trainerParameters) {
        this.agent = agent;
        this.trainerParameters = trainerParameters;
        this.linearFitter=new LinearBatchFitter(trainerParameters.learningRateNonNeuralActor()); //correct?
    }

    public void train(ReplayBuffer<V> buffer ) {

        var batch=buffer.getMiniBatch(trainerParameters.miniBatchSize());
        var ei=new EpisodeInfo<>(batch);
        var timeNoExpMap=ei.getTimeFrequenceData();
        List<Integer> presentTimes=timeNoExpMap.stream().filter(t -> t.value(t)>trainerParameters.minNofSamples).toList();
        int timeChoosen=...
        List<Experience<V>> experiencesAtChosenTime=...
        experiencesAtChosenTime=updateValues
        RealVector weightsAndBias=agent.getCritic();
        Pair<RealMatrix, RealVector> batchData=createData(N_FEAT,experiencesAtChosenTime);
        weightsAndBias=linearFitter.fit(weightsAndBias,batchData);
        agent.setCriticAtTime(timeChoosen,weightsAndBias);

    }

    Pair<RealMatrix, RealVector> createData(int nFeat, List<Experience<V>> experiencesAtChosenTime) {
        int nPoints=experiencesAtChosenTime.size();
        var xMat = new Array2DRowRealMatrix(nPoints, nFeat);
        var yVec = new ArrayRealVector(nPoints);
        int i=0;
        for (Experience<V> experience:experiencesAtChosenTime) {
            StateTrading state=(StateTrading) experience.state();  //todo inte så snyggt
            double[] features = {state.soc()};
            double target = experience.rewardApplied() +
                    trainerParameters.gamma()*agent.readCritic(experience.stateNextApplied());
            xMat.setRow(i, features);
            yVec.setEntry(i, target);
            i++;
        }
        return Pair.create(xMat, yVec);
    }



}
