package mcts_spacegame.model_mcts;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.stream.Collectors;

@ToString
public class SimulationResults {

    @AllArgsConstructor
    @ToString
    public static class SimulationResult {
        public double singleReturn;
        public double valueInGoal;
        public boolean isEndingInFail;
    }

     List<SimulationResult> results;

    public SimulationResults() {
        results=new ArrayList<>();
    }

    public void add(double sumOfReturns, boolean isEndingInFail) {
        results.add(new SimulationResult(sumOfReturns,0,isEndingInFail));
    }

    public void add(double sumOfReturns, double valueInGoal, boolean isEndingInFail) {
                results.add(new SimulationResult(sumOfReturns,valueInGoal,isEndingInFail));
    }

    public List<SimulationResult> getResults() {
        return results;
    }

    public int size() {
        return results.size();
    }

    public boolean areAllSimulationsTerminalFail() {
        return results.stream().allMatch(r -> r.isEndingInFail);
    }

    public OptionalDouble maxReturn() {
        List<Double> returnList = getReturnListForNonFailing();
        return returnList.stream().mapToDouble(Double::doubleValue).max();
    }

    public OptionalDouble averageReturn() {
        List<Double> returnList = getReturnListForNonFailing();
        return returnList.stream().mapToDouble(Double::doubleValue).average();
    }

    public OptionalDouble anyFailingReturn() {
        List<Double> returnList = getReturnsForFailing();
        Random r=new Random();
        return (returnList.size()==0)
                ? OptionalDouble.empty()
                : OptionalDouble.of(returnList.get(r.nextInt(returnList.size())));
    }

    public List<Double> getReturnListForNonFailing() {
        List<SimulationResult> results= nonFailingResults();
        return getReturns(results);
    }

    public List<Double> getReturnsForFailing() {
        List<SimulationResult> results= failingResults();
        return getReturns(results);
    }

    private List<Double> getReturns(List<SimulationResult> results) {
        return results.stream().map(r -> r.singleReturn).collect(Collectors.toList());
    }

    private List<SimulationResult> nonFailingResults() {
        return results.stream().filter(r -> !r.isEndingInFail).collect(Collectors.toList());
    }

    private List<SimulationResult> failingResults() {
        return results.stream().filter(r -> r.isEndingInFail).collect(Collectors.toList());
    }

}
