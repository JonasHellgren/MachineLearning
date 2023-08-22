package multi_step_temp_diff.domain.environments.charge;

import common.RandUtils;
import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("Convert2MethodRef")
@Log
public class SiteStateRules {

    ChargeEnvironmentSettings settings;

    static Map<Predicate<StateInterface<ChargeVariables>>, Supplier<SiteState>> siteStateTable;
    public SiteStateRules(ChargeEnvironmentSettings settings) {
        this.settings =settings;
        ChargeEnvironmentLambdas lambdas=new ChargeEnvironmentLambdas(settings);
        Predicate<StateInterface<ChargeVariables>> isAnySoCBad = (s) ->
                ChargeState.socA.apply(s) < settings.socBad() || ChargeState.socB.apply(s) < settings.socBad();
        Predicate<StateInterface<ChargeVariables>> isTwoAtSamePos = (s) ->
                ChargeState.posA.apply(s).equals(ChargeState.posB.apply(s));
        Predicate<StateInterface<ChargeVariables>> isTwoCharging = (s) ->
                lambdas.isChargePos.test(ChargeState.posA.apply(s)) && lambdas.isChargePos.test(ChargeState.posB.apply(s));
        Predicate<StateInterface<ChargeVariables>> isTimeUp = (s) -> ChargeState.time.apply(s)> settings.maxNofSteps();

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
            log.fine("state = " + state);
            log.fine("Multiple matching rules, nof ="+fcnList.size());
            int randRuleIndex= RandUtils.getRandomIntNumber(0,fcnList.size());
            return fcnList.get(randRuleIndex).get();
        }
        if (fcnList.size()==0) {
            return SiteState.isAllFine;
        }
        return fcnList.get(0).get();
    }


}
