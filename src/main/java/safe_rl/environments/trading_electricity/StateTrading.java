package safe_rl.environments.trading_electricity;
import lombok.AllArgsConstructor;
import safe_rl.domain.environment.aggregates.StateI;

@AllArgsConstructor
public class StateTrading implements StateI<VariablesTrading> {
    public static final int N_CONT_FEAT = 1;
    public static final int INDEX_SOC=0;
    VariablesTrading variables;

    public static StateTrading newFullAndFresh() {
        return new StateTrading(VariablesTrading.newZeroSoCFullSoh());
    }

    public static StateTrading of(VariablesTrading variables) {
        return new StateTrading(variables);
    }

    public static StateTrading allZero() {
        return new StateTrading(VariablesTrading.newTimeSoc(0,0));
    }

    @Override
    public VariablesTrading getVariables() {
        return variables;
    }

    @Override
    public void setVariables(VariablesTrading variables) {
        this.variables=variables;
    }

    @Override
    public StateI<VariablesTrading> copy() {
        return new StateTrading(variables.copy());
    }

    public StateI<VariablesTrading> copyWithDtimeDsocDsoh(double dt,double dSoc, double dSoh) {
        return of(variables.copyWithDtimeDsocDsoh(dt,dSoc,dSoh));
    }

    @Override
    public double[] continuousFeatures() {
        return new double[]{soc()};
    }

    @Override
    public int nContinuousFeatures() {
        return N_CONT_FEAT;
    }

    @Override
    public int[] discreteFeatures() {
        return new int[]{(int) time()};
    }

    @Override
    public void setContinuousFeatures(double[] features) {
        variables=variables.withSoc(features[0]);
    }

    @Override
    public void setDiscreteFeatures(int[] features) {
        variables=variables.withTime(features[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        var v=((StateTrading) o).variables;
        return variables.equalDiscrete(v);
    }

    @Override
    public int hashCode() {
        return variables.hashDiscrete();
    }

    public double time() {
        return  variables.time();
    }

    public double soc() {
        return  variables.soc();
    }

    public double soh() {
        return  variables.soh();
    }

    public double socStart() {
        return  variables.socStart();
    }

    public double dSohSinceStart() {
        return variables.soh()- variables.sohStart();
    }

    @Override
    public String toString() {
        return variables.toString();
    }




}
