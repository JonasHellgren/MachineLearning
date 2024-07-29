package safe_rl.environments.buying_electricity;

import lombok.AllArgsConstructor;
import safe_rl.domain.environment.aggregates.StateI;

@AllArgsConstructor
public class StateBuying implements StateI<VariablesBuying> {

    public static final int N_CONT_FEAT = 1;
    VariablesBuying variables;

    public static StateBuying newZero() {
        return new StateBuying(VariablesBuying.newZero());
    }


    public static StateBuying newSoc(double soc) {
        return new StateBuying(VariablesBuying.newSoc(soc));
    }

    public static StateBuying of(VariablesBuying variables) {
        return new StateBuying(variables);
    }

    @Override
    public VariablesBuying getVariables() {
        return variables;
    }

    @Override
    public void setVariables(VariablesBuying variables) {
        this.variables=variables;
    }

    @Override
    public StateI<VariablesBuying> copy() {
        return new StateBuying(variables.copy());
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
        var v=((StateBuying) o).variables;
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

    public double socStart() {
        return  variables.socStart();
    }

    @Override
    public String toString() {
        return variables.toString();
    }




}
