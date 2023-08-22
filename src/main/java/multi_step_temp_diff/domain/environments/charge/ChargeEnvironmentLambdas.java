package multi_step_temp_diff.domain.environments.charge;

import lombok.extern.java.Log;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Log
public class ChargeEnvironmentLambdas {

    ChargeEnvironmentSettings settings;

    public ChargeEnvironmentLambdas(ChargeEnvironmentSettings settings) {
        this.settings = settings;
    }

    public Predicate<Integer> isAtChargeQuePos = (p) -> p == settings.chargeQuePos();
    public Predicate<Integer> isAtChargeDecisionPos = (p) -> p == settings.chargeDecisionPos();
    public static BiPredicate<Integer, Integer> isMoving = (p, pNew) -> !Objects.equals(p, pNew);
    public static BiPredicate<Integer, Integer> isStill = isMoving.negate();

    public Predicate<Integer> isNonValidAction = (a) -> a > (settings.nofActions() - 1) || a<0;
    public Predicate<Integer> isNonValidPos = (p) -> p < settings.posMin() || p > settings.posMax();
    public Predicate<Double> isNonValidSoC = (s) -> s < settings.socMin() || s > settings.socMax();
    public static  Predicate<Integer> isNonValidTime = (t) -> t < 0;

    public BiPredicate<Integer, Integer> isStillAtChargeQuePos = (pos, posNew) ->
            isAtChargeQuePos.test(pos) && !isMoving.test(pos, posNew);
    public Predicate<Integer> isChargePos = (p) -> settings.chargeNodes().contains(p);

    public static BiPredicate<StateInterface<ChargeVariables>,Integer> isAnyAtNode = (s, n) ->
            s.getVariables().posA==n || s.getVariables().posB==n;

    public  static BiFunction<StateInterface<ChargeVariables>,Integer,Double> socOfAtNode=(s,n) ->
    {
        if (isAnyAtNode.test(s,n)) {
            ChargeVariables variables = s.getVariables();
            return (variables.posA==n)
                    ? variables.socA : variables.socB;
        } else {
            log.warning("None at node = "+n);
            return 0d;
        }
    };

    public BiPredicate<StateInterface<ChargeVariables>, StateInterface<ChargeVariables>>
            isStillAtChargeQuePosFromStates = (s,sNew) -> {
        boolean isA=isStillAtChargeQuePos.test(s.getVariables().posA,sNew.getVariables().posA);
        boolean isB=isStillAtChargeQuePos.test(s.getVariables().posB,sNew.getVariables().posB);
        return isA || isB;
    };

    public Predicate<StateInterface<ChargeVariables>> isAnyAtChargeDecisionPos = (s) -> {
        boolean isA=isAtChargeDecisionPos.test(s.getVariables().posA);
        boolean isB=isAtChargeDecisionPos.test(s.getVariables().posB);
        return isA || isB;
    };

    public Predicate<Integer> isPosInSite=(p) -> settings.siteNodes().contains(p);

}
