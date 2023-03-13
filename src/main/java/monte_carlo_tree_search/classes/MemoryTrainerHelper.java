package monte_carlo_tree_search.classes;

import lombok.extern.java.Log;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is for avoiding code duplication
 */

@Log
public class MemoryTrainerHelper<SSV, AV>  {

    private static final int NOF_EPOCHS_BETWEEN_LOGS = 1000;
    int miniBatchSize;
    double maxError;
    int maxNofEpochs;

    public MemoryTrainerHelper(int miniBatchSize, double maxError, int maxNofEpochs) {
        this.miniBatchSize = miniBatchSize;
        this.maxError = maxError;
        this.maxNofEpochs = maxNofEpochs;
    }

    public void logProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % NOF_EPOCHS_BETWEEN_LOGS == 0 || epoch==0) {
            logEpoch(learningRule, epoch);
        }
    }

     public  void logEpoch(MomentumBackpropagation learningRule, int epoch) {
        log.info("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
    }

     public double getAverageReturn(SimulationResults simulationResults) {
        List<Double> returns= new ArrayList<>(simulationResults.getReturnListForAll());
        return returns.stream().mapToDouble(val -> val).average().orElse(0.0);
    }


    public void trainMemory(NetworkMemoryInterface<SSV, AV> memory,
                            ReplayBuffer<SSV, AV> buffer) {
        int epoch = 0;
        do {
            List<Experience<SSV, AV>> miniBatch=buffer.getMiniBatch(miniBatchSize);
            memory.learn(miniBatch);
            logProgressSometimes(memory.getLearningRule(), epoch++);
        } while (memory.getLearningRule().getTotalNetworkError() > maxError && epoch < maxNofEpochs);
        logEpoch(memory.getLearningRule(), epoch);
    }



}
