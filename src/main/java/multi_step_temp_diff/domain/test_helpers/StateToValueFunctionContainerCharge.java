package multi_step_temp_diff.domain.test_helpers;

import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;

import java.util.function.BiPredicate;
import java.util.function.Function;


public class StateToValueFunctionContainerCharge {

    ChargeEnvironmentLambdas lambdas;
    ChargeEnvironmentSettings settings;
    double socLimit;

    public StateToValueFunctionContainerCharge(ChargeEnvironmentLambdas lambdas,
                                               ChargeEnvironmentSettings settings,
                                               double socLimit ) {
        this.lambdas = lambdas;
        this.settings = settings;
        this.socLimit=socLimit;
    }

    public  Function<ChargeState, Double> limit =(s) -> {
        ChargeVariables vars = s.getVariables();
        BiPredicate<Double,Integer> isBelowSocLimitAndNotChargePos=(soc, pos) ->
                soc<socLimit && !lambdas.isChargePos.test(pos);

        return  (isBelowSocLimitAndNotChargePos.test(vars.socA, vars.posA) ||
                isBelowSocLimitAndNotChargePos.test(vars.socB, vars.posB))
                ? settings.rewardBad()
                :0d;
    };

    public Function<ChargeState, Double> fixedAtZero =(s) -> 0d;

}
