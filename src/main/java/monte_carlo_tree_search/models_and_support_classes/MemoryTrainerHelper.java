package monte_carlo_tree_search.models_and_support_classes;

import lombok.extern.java.Log;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is for avoiding code duplication
 */

@Log
public class MemoryTrainerHelper<S, A>  {

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
        log.fine("Epoch " + epoch + ", error=" + learningRule.getTotalNetworkError());
    }

     public double getAverageReturn(SimulationResults simulationResults) {
        List<Double> returns= new ArrayList<>(simulationResults.getReturnListForAll());
        return returns.stream().mapToDouble(val -> val).average().orElse(0.0);
    }


    public void trainMemory(NetworkMemoryInterface<S, A> memory,
                            ReplayBuffer<S, A> buffer) {
        int epoch = 0;
        do {
            List<Experience<S, A>> miniBatch=buffer.getMiniBatch(miniBatchSize);
            memory.learn(miniBatch);
            logProgressSometimes(memory.getLearningRule(), epoch++);
        } while (memory.getLearningRule().getTotalNetworkError() > maxError && epoch < maxNofEpochs);
        logEpoch(memory.getLearningRule(), epoch);
    }



}
