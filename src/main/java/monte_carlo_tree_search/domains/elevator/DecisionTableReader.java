package monte_carlo_tree_search.domains.elevator;

import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Log
public class DecisionTableReader {
    private static final int BACKUP = 0;

    Map<BiPredicate<Integer,Integer>, BiFunction<Integer,Integer,Integer>> decisionTable;

    public DecisionTableReader(Map<BiPredicate<Integer,Integer>, BiFunction<Integer,Integer,Integer>> decisionTable) {
        this.decisionTable=decisionTable;
    }

    public  Integer readSingleActionChooseRandomIfMultiple(Integer speed, Integer pos) {
        List<Integer> actionValues= new ArrayList<>(readAllAvailableActions(speed, pos));
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

    public Set<Integer> readAllAvailableActions(Integer speed, Integer pos) {
        List<Integer> integerList=new ArrayList<>();
        List<BiFunction<Integer, Integer, Integer>> fcnList = getBiFunctions(speed, pos);
        for (BiFunction<Integer, Integer, Integer> fcn:fcnList) {
            integerList.add(fcn.apply(speed,pos));
        }
        return new HashSet<>(integerList);
    }

    @NotNull
    private List<BiFunction<Integer, Integer, Integer>> getBiFunctions(Integer speed, Integer pos) {
        return decisionTable.entrySet().stream()
                .filter(e -> e.getKey().test(speed, pos))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }


    private void logging(Integer speed, Integer pos, List<BiFunction<Integer, Integer, Integer>> fcnList) {
        log.info("Multiple matching rules, nof ="+ fcnList.size()+". Applying random.");
        for (int i = 0; i < fcnList.size() ; i++) {
            log.info("i = "+i+", a = " + fcnList.get(i).apply(speed, pos));
        }
    }
}
