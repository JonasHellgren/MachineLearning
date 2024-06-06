package multi_agent_rl.environments.apple;

import com.google.common.base.Preconditions;
import common.math.Discrete2DPos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import multi_agent_rl.domain.abstract_classes.ObservationI;
import multi_agent_rl.domain.abstract_classes.StateI;

@AllArgsConstructor
public class StateApple implements StateI<VariablesStateApple,VariablesObservationApple> {

    @Setter
    @Getter
    VariablesStateApple variables;

    public static StateApple of(Discrete2DPos posApple, Discrete2DPos posA, Discrete2DPos posB, AppleSettings settings) {
        VariablesStateApple variablesApple = VariablesStateApple.builder()
                .posApple(posApple).posA(posA).posB(posB)
                .build();
        Preconditions.checkArgument(variablesApple.isInBounds(settings),"Non correct position");
        return new StateApple(variablesApple);
    }

    public static boolean posInBounds(Discrete2DPos pos, AppleSettings settings) {
        return VariablesStateApple.isPosInBounds(pos,settings);
    }

    @Override
    public StateI<VariablesStateApple,VariablesObservationApple> copy() {
        return new StateApple(variables.copy());
    }

    @Override
    public ObservationI<VariablesObservationApple> getObservation(String id) {
        return ObservationApple.of(this,id.equals("A"));
    }


    public Discrete2DPos posA() {
        return variables.posA();
    }

    public Discrete2DPos posB() {
        return variables.posB();
    }

    public Discrete2DPos posApple() {
        return variables.posApple();
    }

    @Override
    public boolean equals(Object o) {
        StateApple other=(StateApple) o;
        return posA().equals(other.posA()) &&
                posB().equals(other.posB()) &&
                posApple().equals(other.posApple());
    }

    @Override
    public String toString() {
        return variables.toString();
    }



}
