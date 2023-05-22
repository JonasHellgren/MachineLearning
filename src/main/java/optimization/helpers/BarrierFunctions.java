package optimization.helpers;

import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
public class BarrierFunctions {

    double penCoeff;
    Set<String> TYPES= Set.of("quad");

    Function<Double,Double> barrierFunction;

    public BarrierFunctions(double penCoeff,String type) {
        throwIfNotValidType(type);
        this.penCoeff = penCoeff;
        this.barrierFunction=setFunction(type);
    }

    private void throwIfNotValidType(String type) {
        if (!TYPES.contains(type)) {
            throw new IllegalArgumentException("Illegal type, allowed types = "+TYPES);
        }
    }

    private Map<Predicate<String>, Function<Double,Double>> createFunctionTable(double penCoeff) {
        Map<Predicate<String>, Function<Double,Double>> functionTable = new HashMap<>();
        Predicate<String> isQuad=(s) -> s.equals("quad");
        functionTable.put(isQuad,(cv) -> (cv >0)  ? penCoeff *Math.pow(cv,2): 0d);
        return functionTable;
    }


    public Double process(Double constraintValue) {
        return barrierFunction.apply(constraintValue);
    }
    public  Function<Double,Double> setFunction(String type) {
        Map<Predicate<String>, Function<Double,Double>> functionTable= createFunctionTable(penCoeff);
        List<Function<Double,Double>> fcnList=functionTable.entrySet().stream()
                .filter(e -> e.getKey().test(type))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
        throwIfMotivated(fcnList);
        return fcnList.get(0);
    }

    private static void throwIfMotivated(List<Function<Double, Double>> fcnList) {
        if (fcnList.size()>1) {
            throw  new RuntimeException("Multiple matching rules, nof ="+ fcnList.size());
        }

        if (fcnList.size()==0) {
            throw  new RuntimeException("No matching rule, using backup");
        }
    }


}
