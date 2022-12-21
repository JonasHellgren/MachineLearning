package monte_carlo_tree_search.domains.models_battery_cell;

import common.Interpolator;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentCell implements EnvironmentGenericInterface<CellVariables,Integer> {

    @Builder
    @ToString
    @Getter
    public static class CellResults {
        double current;
        double newSoC;
        double temperatureTimeDerivative;
        double newTemperature;
        double newTime;
        double ocv;
        double voltage;
        double power;
        boolean isToHighVoltage;
        boolean isToHighTemperature;
        boolean isToHighPowerCell;
        boolean isTimeUp;
    }

    CellSettings s;
    CellResults cellResults;
    Interpolator interpolatorOCV;

    public EnvironmentCell(CellSettings s) {
        this.s = s;
        this.interpolatorOCV=new Interpolator(s.socValuesForOcvCurve, s.ocvValuesForOcvCurve);
     }

    public CellResults getCellResults() {
        return cellResults;
    }

    @Override
    public StepReturnGeneric<CellVariables> step(ActionInterface<Integer> action, StateInterface<CellVariables> state) {
        CellVariables v=state.getVariables();

        ActionCell actionCastedToGetRelativeCurrentMethod=(ActionCell) action;
        double current=s.maxCurrent*actionCastedToGetRelativeCurrentMethod.getRelativeCurrent();
        double newSoC=v.SoC+current*s.dt/s.capacity;
        double temperatureTimeDerivative = calculateTemperatureTimeDerivative(state, current);
        double newTemperature=v.temperature+temperatureTimeDerivative*s.dt;
        double newTime=v.time+s.dt;

        StateInterface<CellVariables> newState= new StateCell(CellVariables.builder()
                .SoC(newSoC).temperature(newTemperature).time(newTime).build());

        double ocv=interpolatorOCV.interpLinear(new double[]{v.SoC})[0];
        double voltage=ocv+current*s.resistance;
        double power=voltage*current;

        boolean isToHighVoltage=voltage>s.maxVoltage;
        boolean isToHighPowerCell=Math.abs(power)>Math.abs(s.powerCellMax);
        boolean isToHighTemperature=newTemperature>s.maxTemperature;
        boolean isFail=isToHighVoltage || isToHighTemperature || isToHighPowerCell;
        boolean isTimeUp=newTime>=s.maxTime;

        double reward=newSoC-v.SoC;

        this.cellResults=CellResults.builder()
                .current(current)
                .newSoC(newSoC)
                .temperatureTimeDerivative(temperatureTimeDerivative)
                .newTemperature(newTemperature)
                .newTime(newTime)
                .ocv(ocv)
                .voltage(voltage)
                .power(power)
                .isToHighVoltage(isToHighVoltage)
                .isToHighTemperature(isToHighTemperature)
                .isToHighPowerCell(isToHighPowerCell)
                .isTimeUp(isTimeUp)
                .build();

        return StepReturnGeneric.<CellVariables>builder()
                .newState(newState)
                .isFail(isFail)
                .isTerminal(isFail || isTimeUp)
                .reward(reward)
                .build();

    }

    private double calculateTemperatureTimeDerivative(StateInterface<CellVariables> state, double current) {
        double Qgen=Math.pow(current,2)* s.resistance;
        double Qloss= s.heatTransferCoefficient* s.heatArea*(state.getVariables().temperature- s.temperatureAmbient);
        return (Qgen-Qloss)/s.heatCapacityCoefficient;
    }



}
