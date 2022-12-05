package mcts_spacegame.model_mcts;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

@ToString
public class SimulationResults {

    @AllArgsConstructor
    @ToString
    public static class SimulationResult {
        public double sumOfReturns;
        public boolean isEndingInFail;
    }

     List<SimulationResult> results;

    public SimulationResults() {
        results=new ArrayList<>();
    }

    public void add(double sumOfReturns, boolean isEndingInFail) {
                results.add(new SimulationResult(sumOfReturns,isEndingInFail));
    }

    public List<SimulationResult> getResults() {
        return results;
    }

}
