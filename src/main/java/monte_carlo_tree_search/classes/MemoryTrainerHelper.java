package monte_carlo_tree_search.classes;

import lombok.extern.java.Log;
import monte_carlo_tree_search.domains.cart_pole.CartPoleVariables;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.network_training.Experience;
import monte_carlo_tree_search.network_training.ReplayBuffer;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import java.util.ArrayList;
import java.util.List;

@Log
public class MemoryTrainerHelper<SSV, AV>  {


    public void logProgressSometimes(MomentumBackpropagation learningRule, int epoch) {
        if (epoch % 1000 == 0 || epoch==0) {
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

    /*
    public void trainMemory(NetworkMemoryInterface<SSV> memory,
                            ReplayBuffer<SSV, AV> buffer) {
        int epoch = 0;
        do {
            List<Experience<SSV, AV>> miniBatch=buffer.getMiniBatch(miniBatchSize);
            memory.learn(miniBatch);
            helper.logProgressSometimes(memory.getLearningRule(), epoch++);
        } while (memory.getLearningRule().getTotalNetworkError() > maxError && epoch < maxNofEpochs);
        helper.logEpoch(memory.getLearningRule(), epoch);
    }

    */

}
