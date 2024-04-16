package safe_rl.environments.buying_electricity;

import lombok.AllArgsConstructor;
import org.bytedeco.opencv.presets.opencv_core;
import safe_rl.domain.abstract_classes.StateI;

@AllArgsConstructor
public class StateBuying implements StateI<VariablesBuying> {

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
