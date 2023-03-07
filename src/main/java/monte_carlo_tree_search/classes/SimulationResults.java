package monte_carlo_tree_search.classes;

import common.ListUtils;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Data container for the simulate step
 */

public class SimulationResults {

    private static final String NEW_LINE = System.getProperty("line.separator");

    @AllArgsConstructor
    @ToString
    public static class SimulationResult {
        public double singleReturn;
        public boolean isEndingInFail;
    }

     List<SimulationResult> results;

    public SimulationResults() {
        results=new ArrayList<>();
    }

    public static SimulationResults newEmpty() {
        return new SimulationResults();
    }

    public void add(double sumOfReturns, boolean isEndingInFail) {
        results.add(new SimulationResult(sumOfReturns,isEndingInFail));
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

    public OptionalDouble maxReturnFromNonFailing() {
        List<Double> returnList = getReturnListForNonFailing();
        return  returnList.stream()
                .mapToDouble(Double::doubleValue)
                .max();
    }

    public OptionalDouble maxReturnFromAll() {
        List<Double> returnList = getReturnListForAll();
        return  returnList.stream()
                .mapToDouble(Double::doubleValue)
                .max();
    }

    public OptionalDouble averageReturnFromNonFailing() {
        List<Double> returnList = getReturnListForNonFailing();
        return  returnList.stream()
                .mapToDouble(Double::doubleValue)
                .average();
    }

    public OptionalDouble averageReturnFromAll() {
        List<Double> returnList = getReturnListForAll();
        return  returnList.stream()
                .mapToDouble(Double::doubleValue)
                .average();
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

    public List<Double> getReturnListForAll() {
        List<SimulationResult> results= allResults();
        return getReturns(results);
    }

    public List<Double> getReturnsForFailing() {
        List<SimulationResult> results= failingResults();
        return getReturns(results);
    }

    private List<Double> getReturns(List<SimulationResult> results) {
        return results.stream().map(r -> r.singleReturn).collect(Collectors.toList());
    }

    private List<SimulationResult> allResults() {
        return new ArrayList<>(results);
    }

    private List<SimulationResult> nonFailingResults() {
        return results.stream().filter(r -> !r.isEndingInFail).collect(Collectors.toList());
    }

    private List<SimulationResult> failingResults() {
        return results.stream().filter(r -> r.isEndingInFail).collect(Collectors.toList());
    }

    @Override
    public String toString() {

        StringBuilder sb=new StringBuilder();
        sb.append(NEW_LINE);
        for (SimulationResult res:results) {
            sb.append("Result = ").append(res);
            sb.append(NEW_LINE);
        }
        return sb.toString();
    }

}
