package multi_step_td.charge;

import common.RandUtils;
import multi_step_temp_diff.environments.ChargeEnvironment;
import multi_step_temp_diff.environments.ChargeState;
import multi_step_temp_diff.environments.ChargeVariables;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;
import java.util.function.*;

public class TestChargeHelper {

    public static Function<StepReturn<ChargeVariables>, Integer> posNewA = (sr) -> ChargeState.posA.apply(sr.newState);
    public static Function<StepReturn<ChargeVariables>, Double> socNewA = (sr) -> ChargeState.socA.apply(sr.newState);
    public static Function<StepReturn<ChargeVariables>, Integer> posNewB = (sr) -> ChargeState.posB.apply(sr.newState);
    public static Function<StepReturn<ChargeVariables>, Double> socNewB = (sr) -> ChargeState.socB.apply(sr.newState);
    public static Function<StepReturn<ChargeVariables>, Integer> time = (sr) -> ChargeState.time.apply(sr.newState);

    public static BiConsumer<StateInterface<ChargeVariables>,Integer> setPosA=(s, p) -> s.getVariables().posA=p;
    public static BiConsumer<StateInterface<ChargeVariables>,Integer> setPosB=(s,p) -> s.getVariables().posB=p;
    public static BiConsumer<StateInterface<ChargeVariables>,Double> setSocA=(s, soc) -> s.getVariables().socA=soc;
    public static BiConsumer<StateInterface<ChargeVariables>,Double> setSocB=(s, soc) -> s.getVariables().socB=soc;

    public static BiPredicate<StateInterface<ChargeVariables>,StateInterface<ChargeVariables>> samePosA = (s0,s1) ->
            ChargeState.posA.apply(s0).equals(ChargeState.posA.apply(s1));
    public static BiPredicate<StateInterface<ChargeVariables>,StateInterface<ChargeVariables>> samePosB = (s0,s1) ->
            ChargeState.posB.apply(s0).equals(ChargeState.posB.apply(s1));
    public static BiPredicate<StateInterface<ChargeVariables>,StateInterface<ChargeVariables>> sameSoCA = (s0,s1) ->
            ChargeState.socA.apply(s0).equals(ChargeState.socA.apply(s1));
    public static BiPredicate<StateInterface<ChargeVariables>,StateInterface<ChargeVariables>> sameSoCB = (s0,s1) ->
            ChargeState.socB.apply(s0).equals(ChargeState.socB.apply(s1));

    public static Supplier<Integer> randomAction = () -> RandUtils.getRandomIntNumber(0, ChargeEnvironment.NOF_ACTIONS);

    public static void printStepReturn(StepReturn stepReturn) {
        System.out.println("stepReturn = " + stepReturn);
    }

    public static void printAction(int action) {
        System.out.println("action = " + action);
    }

}
