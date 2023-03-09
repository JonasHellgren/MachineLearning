package monte_carlo_tree_search.domains.elevator;

import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Log
public class DecisionTableReader {
    private static final int BACKUP = 0;

    Map<ElevatorTriPredicates.TriPredicate<Integer,Integer, Double>, Supplier<Integer>> decisionTable;

    public DecisionTableReader(Map<ElevatorTriPredicates.TriPredicate<Integer,Integer, Double>, Supplier<Integer>> decisionTable) {
        this.decisionTable=decisionTable;
    }

    public  Integer readSingleActionChooseRandomIfMultiple(Integer speed, Integer pos, Double SoE) {
        List<Integer> actionValues= new ArrayList<>(readAllAvailableActions(speed, pos, SoE));
        if (actionValues.size()>1) {
           // logging(speed, pos, fcnList);
            return actionValues.get(RandomUtils.nextInt(0,actionValues.size()));
        }
        if (actionValues.size()==0) {
            log.warning("No matching rule, using backup. Speed = "+speed+", pos = "+pos);
            throw  new RuntimeException();
           // return BACKUP;
        }
        return actionValues.get(0);
    }

    public Set<Integer> readAllAvailableActions(Integer speed, Integer pos, Double SoE) {
        List<Integer> integerList=new ArrayList<>();
        List<Supplier<Integer>> suppliers = getBiFunctions(speed, pos, SoE);
        for (Supplier<Integer> fcn:suppliers) {
            integerList.add(fcn.get());
        }
        return new HashSet<>(integerList);
    }

    @NotNull
    private List<Supplier<Integer>> getBiFunctions(Integer speed, Integer pos, Double SoE) {
        return decisionTable.entrySet().stream()
                .filter(e -> e.getKey().test(speed, pos, SoE))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    private void logging(List<Supplier<Integer>> fcnList) {
        log.info("Multiple matching rules, nof ="+ fcnList.size()+". Applying random.");
        for (int i = 0; i < fcnList.size() ; i++) {
            log.info("i = "+i+", a = " + fcnList.get(i).get());
        }
    }
}
