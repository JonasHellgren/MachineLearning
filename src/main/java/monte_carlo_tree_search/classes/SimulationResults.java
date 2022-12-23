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
        public double valueInTerminalState;
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

    public OptionalDouble maxReturnFromNonFailing() {
        List<Double> returnList = getReturnListForNonFailing();
        List<Double> terminalValueList = getTerminalStateValuesForNonFailing();
        return ListUtils.sumListElements(returnList, terminalValueList).stream()
                .mapToDouble(Double::doubleValue)
                .max();
    }

    public OptionalDouble maxReturnFromAll() {
        List<Double> returnList = getReturnListForAll();
        List<Double> terminalValueList = getTerminalStateValuesForAll();
        return ListUtils.sumListElements(returnList, terminalValueList).stream()
                .mapToDouble(Double::doubleValue)
                .max();
    }

    public OptionalDouble averageReturnFromNonFailing() {
        List<Double> returnList = getReturnListForNonFailing();
        List<Double> terminalValueList = getTerminalStateValuesForNonFailing();
        return ListUtils.sumListElements(returnList, terminalValueList).stream()
                .mapToDouble(Double::doubleValue)
                .average();
    }

    public OptionalDouble averageReturnFromAll() {
        List<Double> returnList = getReturnListForAll();
        List<Double> terminalValueList = getTerminalStateValuesForAll();
        return ListUtils.sumListElements(returnList, terminalValueList).stream()
                .mapToDouble(Double::doubleValue)
                .average();
    }

    public OptionalDouble anyFailingReturn() {
        List<Double> returnList = getReturnsForFailing();
        List<Double> terminalValueList = getTerminalStateValuesForFailing();
        List<Double> sumList=ListUtils.sumListElements(returnList,terminalValueList);
        Random r=new Random();
        return (returnList.size()==0)
                ? OptionalDouble.empty()
                : OptionalDouble.of(sumList.get(r.nextInt(sumList.size())));
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

    public List<Double> getTerminalStateValuesForNonFailing() {
        List<SimulationResult> results= nonFailingResults();
        return getTerminalStateValues(results);
    }


    public List<Double> getTerminalStateValuesForAll() {
        List<SimulationResult> results= allResults();
        return getTerminalStateValues(results);
    }



    public List<Double> getTerminalStateValuesForFailing() {
        List<SimulationResult> results= failingResults();
        return getTerminalStateValues(results);
    }

    private List<Double> getReturns(List<SimulationResult> results) {
        return results.stream().map(r -> r.singleReturn).collect(Collectors.toList());
    }

    private List<Double> getTerminalStateValues(List<SimulationResult> results) {
        return results.stream().map(r -> r.valueInTerminalState).collect(Collectors.toList());
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
