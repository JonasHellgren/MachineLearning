package safe_rl.environments.buying_electricity;

import lombok.AllArgsConstructor;
import safe_rl.domain.abstract_classes.StateI;

@AllArgsConstructor
public class StateBuying implements StateI<VariablesBuying> {

    public static final int N_CONT_FEAT = 1;
    VariablesBuying variables;

    public static StateBuying newZero() {
        return new StateBuying(VariablesBuying.newZero());
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
        return new StateBuying(variables);
    }

    @Override
    public double[] continousFeatures() {
        return new double[]{soc()};
    }

    @Override
    public int nContinousFeatures() {
        return N_CONT_FEAT;
    }

    @Override
    public int[] discretFeatures() {
        return new int[]{(int) time()};
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        var v=((StateBuying) o).variables;
        return variables.equals(v);
    }

    @Override
    public int hashCode() {
        return variables.hashCode();
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
