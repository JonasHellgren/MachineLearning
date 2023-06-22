package multi_step_temp_diff.environment_helpers;

import multi_step_temp_diff.environments.ChargeEnvironment;
import multi_step_temp_diff.environments.ChargeState;
import org.apache.arrow.flatbuf.Int;
import org.apache.commons.math3.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PositionTransitionRules {

     Map<BiPredicate<Integer,Boolean>, Function<Integer,Integer>> transitionTable;  //pos, isObstacle -> posNew(command)
    Predicate<Pair<Integer,Integer>> isAtPos=(p) -> p.getFirst().equals(p.getSecond());
    BiPredicate<Pair<Integer,Integer>,Boolean> isAtPosNoObstacle=(p,o) -> isAtPos.test(p) && !o;
    BiPredicate<Pair<Integer,Integer>,Boolean> isAtPosObstacle=(p,o) -> isAtPos.test(p) && o;


    public PositionTransitionRules() {
        this.transitionTable = new HashMap<>();
        transitionTable.put((p,o) -> isAtPos.test(new Pair<>(p,10)), (c) -> (c==0) ? 11 : 20 );
        transitionTable.put((p,o) -> isAtPosNoObstacle.test(new Pair<>(p,18),o), (c) -> (c==0) ? 18 : 19 );
        transitionTable.put((p,o) -> isAtPosObstacle.test(new Pair<>(p,18),o), (c) -> 18 );
        transitionTable.put((p,o) -> isAtPos.test(new Pair<>(p,20)), (c) -> (c==0) ? 20 : 21 );
        transitionTable.put((p,o) -> isAtPosNoObstacle.test(new Pair<>(p,29),o), (c) -> (c==0) ? 29 : 19 );
        transitionTable.put((p,o) -> isAtPosObstacle.test(new Pair<>(p,29),o), (c) -> 29 );
        transitionTable.put((p,o) -> isAtPos.test(new Pair<>(p,30)), (c) -> 30 ); //trap
    }

    public int getNewPos(int pos, boolean isObstacle, int command) {
        List<Function<Integer,Integer>> fcnList= transitionTable.entrySet().stream()
                .filter(e -> e.getKey().test(pos,isObstacle)).map(Map.Entry::getValue)
                .collect(Collectors.toList());
        if (fcnList.size()>1) {
            throw new RuntimeException("Multiple matching rules, nof ="+fcnList.size());
        }
        if (fcnList.size()==0) {
            return pos+1;
        }
        return fcnList.get(0).apply(command);
    }

}
