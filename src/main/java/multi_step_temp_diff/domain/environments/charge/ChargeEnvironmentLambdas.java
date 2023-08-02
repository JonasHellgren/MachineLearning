package multi_step_temp_diff.domain.environments.charge;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ChargeEnvironmentLambdas {

    ChargeEnvironmentSettings settings;

    public ChargeEnvironmentLambdas(ChargeEnvironmentSettings settings) {
        this.settings = settings;
    }

    public Predicate<Integer> isAtChargeQuePos = (p) -> p == settings.chargeQuePos();
    public static BiPredicate<Integer, Integer> isMoving = (p, pNew) -> !Objects.equals(p, pNew);

    public Predicate<Integer> isNonValidAction = (a) -> a > (settings.nofActions() - 1) || a<0;
    public Predicate<Integer> isNonValidPos = (p) -> p < settings.posMin() || p > settings.posMax();
    public Predicate<Double> isNonValidSoC = (s) -> s < settings.socMin() || s > settings.socMax();
    public static  Predicate<Integer> isNonValidTime = (t) -> t < 0;

    public BiPredicate<Integer, Integer> isStillAtChargeQuePos = (pos, posNew) ->
            isAtChargeQuePos.test(pos) && !isMoving.test(pos, posNew);
    public Predicate<Integer> isChargePos = (p) -> settings.chargeNodes().contains(p);

}
