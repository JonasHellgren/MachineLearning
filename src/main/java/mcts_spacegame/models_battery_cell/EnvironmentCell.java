package mcts_spacegame.models_battery_cell;

public class EnvironmentCell implements EnvironmentGenericInterface<StateCell,ActionCell> {

    CellSettings s;

    public EnvironmentCell(CellSettings s) {
        this.s = s;
    }

    @Override
    public StepReturnGeneric<StateCell> step(ActionCell action, StateCell state) {
        CellVariables v=state.variables;

        double current=s.maxCurrent*action.getRelativeCurrent();
        double newSoC=v.SoC+current*s.dt/s.capacity;
        double temperatureTimeDerivative = calculateTemperatureTimeDerivate(state, current);
        double newTemperature=v.temperature+temperatureTimeDerivative*s.dt;
        double newTime=v.time+s.dt;

        StateCell newState= new StateCell(CellVariables.builder()
                .SoC(newSoC).temperature(newTemperature).time(newTime).build());

        double ocv=s.ocv0+v.SoC*(s.ocv1-s.ocv0);
        double voltage=ocv+current*s.resistance;

        boolean isToHighVoltage=voltage>s.maxVoltage;
        boolean isToHighTemperature=newTemperature>s.maxTemperature;
        boolean isTimeUp=newTime>=s.maxTime;

        double reward=newSoC-v.SoC;

        return StepReturnGeneric.<StateCell>builder()
                .newState(newState)
                .isFail(isToHighVoltage || isToHighTemperature)
                .isTerminal(isToHighVoltage || isToHighTemperature || isTimeUp)
                .reward(reward)
                .build();

    }

    private double calculateTemperatureTimeDerivate(StateCell state, double current) {
        double Qgen=Math.pow(current,2)* s.resistance;
        double Qloss= s.heatTransferCoefficient* s.heatArea*(state.variables.temperature- s.temperatureAmbient);
        return (Qgen-Qloss)/s.heatCapacityCoefficient;
    }
}
