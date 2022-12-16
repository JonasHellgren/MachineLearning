package mcts_spacegame.models_battery_cell;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import mcts_spacegame.model_mcts.StateInterface;

@Builder
@ToString
@EqualsAndHashCode
public class StateCell implements StateInterface {

    public double SoC;
    public double temperature;
    public double time;

    @Override
    public StateInterface copy() {
        return null;
    }

    @Override
    public void SetFromReturn() {

    }
}
