package multi_step_temp_diff.domain.environments.charge;

import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import org.apache.commons.lang3.Range;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SiteStateRules {

    public static final int MAX_NOF_STEPS = 100;
    static Range<Integer> CHARGE_POSITIONS = Range.between(21, 29);  //todo from env, test inclusive
    public static final double SOC_BAD = 0.1;

    static Map<Predicate<StateInterface<ChargeVariables>>, Supplier<SiteState>> siteStateTable;

    public static Predicate<Integer> isChargePos = (p) -> CHARGE_POSITIONS.contains(p);

    Predicate<StateInterface<ChargeVariables>> isAnySoCBad = (s) ->
            ChargeState.socA.apply(s) < SOC_BAD || ChargeState.socB.apply(s) < SOC_BAD;
    Predicate<StateInterface<ChargeVariables>> isTwoAtSamePos = (s) ->
            ChargeState.posA.apply(s).equals(ChargeState.posB.apply(s));
    Predicate<StateInterface<ChargeVariables>> isTwoCharging = (s) ->
            isChargePos.test(ChargeState.posA.apply(s)) && isChargePos.test(ChargeState.posB.apply(s));
    Predicate<StateInterface<ChargeVariables>> isTimeUp = (s) -> ChargeState.time.apply(s)> MAX_NOF_STEPS;  //todo from env


    public SiteStateRules() {
        siteStateTable = new HashMap<>();
        siteStateTable.put( (s) -> isAnySoCBad.test(s) ,() -> SiteState.isAnySoCBad);
        siteStateTable.put( (s) -> isTwoAtSamePos.test(s) ,() -> SiteState.isTwoAtSamePos);
        siteStateTable.put( (s) -> isTwoCharging.test(s) ,() -> SiteState.isTwoCharging);
        siteStateTable.put( (s) -> isTimeUp.test(s) ,() -> SiteState.isTimeUp);
    }

    public  SiteState getSiteState(StateInterface<ChargeVariables> state) {
        List<Supplier<SiteState>> fcnList= siteStateTable.entrySet().stream()
                .filter(e -> e.getKey().test(state)).map(Map.Entry::getValue)
                .toList();
        if (fcnList.size()>1) {
            throw new RuntimeException("Multiple matching rules, nof ="+fcnList.size());
        }
        if (fcnList.size()==0) {
            return SiteState.isAllFine;
        }
        return fcnList.get(0).get();
    }


}
