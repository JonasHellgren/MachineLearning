package monte_carlo_tree_search.domains.elevator;

import lombok.extern.java.Log;
import org.apache.commons.lang3.RandomUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public  Integer process(Integer speed, Integer pos) {

        List<BiFunction<Integer,Integer,Integer>> fcnList=decisionTable.entrySet().stream()
                .filter(e -> e.getKey().test(speed,pos))
                .map(e -> e.getValue())
                .collect(Collectors.toList());

        if (fcnList.size()>1) {
           // logging(speed, pos, fcnList);
            return fcnList.get(RandomUtils.nextInt(0,fcnList.size())).apply(speed,pos);
        }

        if (fcnList.size()==0) {
            log.warning("No matching rule, using backup. Speed = "+speed+", pos = "+pos);
            throw  new RuntimeException();
            //return BACKUP;
        }

        return fcnList.get(0).apply(speed,pos);
    }

    private void logging(Integer speed, Integer pos, List<BiFunction<Integer, Integer, Integer>> fcnList) {
        log.info("Multiple matching rules, nof ="+ fcnList.size()+". Applying random.");
        for (int i = 0; i < fcnList.size() ; i++) {
            log.info("i = "+i+", a = " + fcnList.get(i).apply(speed, pos));
        }
    }
}
