package multi_step_td.charge;

import multi_step_temp_diff.environments.ChargeState;
import multi_step_temp_diff.environments.ChargeVariables;
import multi_step_temp_diff.interfaces_and_abstract.StateInterface;
import multi_step_temp_diff.models.StepReturn;

import java.util.function.BiConsumer;
import java.util.function.Function;

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


}
