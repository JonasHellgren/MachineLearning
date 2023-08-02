package multi_step_temp_diff.domain.environments.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import org.apache.commons.math3.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class PositionTransitionRules {

    public static final int DUMMY_POS = 0;
    ChargeEnvironmentSettings settings;

    Map<BiPredicate<Integer,Boolean>, BiFunction<Integer,Integer,Integer>> transitionTable;  //pos, isObstacle -> posNew(pos,command)
    BiPredicate<Pair<Integer,Integer>,Boolean> isAtPos=(p,o) -> p.getFirst().equals(p.getSecond());
    BiPredicate<Pair<Integer,Integer>,Boolean> isAtPosNoObstacle=(p,o) -> isAtPos.test(p,o) && !o;
    BiPredicate<Pair<Integer,Integer>,Boolean> isAtPosObstacle=(p,o) -> isAtPos.test(p,o) && o;
    BiPredicate<Pair<Integer,Integer>,Boolean> isAtTrap=(p,o) -> settings.isAtTrapNode(p.getFirst());

    public PositionTransitionRules(ChargeEnvironmentSettings settings) {
        this.settings = settings;
        this.transitionTable = new HashMap<>();
        addRuleToTable(7,7,8);
        addRuleToTable(10,11,20);
        addRuleToTable(20,20,30);
        addRuleToTable(30,40,40);
        addRuleToTable(40,50,50);
        addRuleToTable(51,52,52);
        addRuleToTable(52,42,42);
        addRuleToTable(42,32,32);
        addRuleToTable(32,22,22);
        addRuleToTable(22,22,12);
        addRuleToTable(19,0,0);
        transitionTable.put((p,o) -> isAtPosNoObstacle.test(new Pair<>(p,8),o), (p,c) -> 9);
        transitionTable.put((p,o) -> isAtPosObstacle.test(new Pair<>(p,8),o), (p,c) -> 8 );
        transitionTable.put((p,o) -> isAtTrap.test(new Pair<>(p, DUMMY_POS),false), (p, c) -> p ); //trap
    }

    private void addRuleToTable(int pos, int posC0, int posC1) {
        transitionTable.put((p, o) -> isAtPos.test(new Pair<>(p, pos), false), (p, c) -> (c == 0) ? posC0 : posC1);
    }

    public int getNewPos(int pos, boolean isObstacle, int command) {
        List<BiFunction<Integer,Integer,Integer>> fcnList= transitionTable.entrySet().stream()
                .filter(e -> e.getKey().test(pos,isObstacle)).map(Map.Entry::getValue)
                .toList();
        if (fcnList.size()>1) {
            throw new RuntimeException("Multiple matching rules, nof ="+fcnList.size());
        }
        if (fcnList.size()==0) {
            return pos+1;
        }
        return fcnList.get(0).apply(pos,command);
    }

}
