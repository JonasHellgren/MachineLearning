package monte_carlo_tree_search.domains.models_battery_cell;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CellSettings {

    static final double MAX_CURRENT_DEFAULT=200;
    static final double RESISTANCE_DEFAULT=0.01;
    static final double CAPACITY_DEFAULT=36_000;  //10 Ah
    static final double MAX_TEMPERATURE=50;   //Celsius
    static final double MAX_VOLTAGE=4.1;
    static final double MAX_POWER_CHARGER=250_000;
    static final int NOF_CELLS=250;
    static final double MAX_POWER_CELL=MAX_POWER_CHARGER/(double) NOF_CELLS;
    //https://www.batterydesign.net/specific-heat-capacity-of-lithium-ion-cells/
    static final double HEAT_CAPACITY_DEFAULT=500;  //J/kg
    //https://iopscience.iop.org/article/10.1088/1757-899X/53/1/012014/pdf
    //https://core.ac.uk/download/pdf/199217131.pdf
    static final double HEAT_TRANSFER_DEFAULT=19;  //W/m^K
    static final double HEAT_AREA_DEFAULT=0.2;  //m^2
    static final double[] SOC_VALUES =new double[]{0, 0.2, 0.8, 1};
    static final double[] OCV_VALUES =new double[]{1, 2.39, 2.4, 4};
    static final double DT_DEFAULT=10;
    static final double TEMP_AMB_DEFAULT=20;
    static final double MAX_TIME_DEFAULT=100;

    public static CellSettings newDefault() {
        return CellSettings.builder().build();
    }

    @Builder.Default
    double maxCurrent=MAX_CURRENT_DEFAULT;
    @Builder.Default
    double resistance=RESISTANCE_DEFAULT;
    @Builder.Default
    double capacity=CAPACITY_DEFAULT;
    @Builder.Default
    double maxTemperature=MAX_TEMPERATURE;
    @Builder.Default
    double maxVoltage=MAX_VOLTAGE;
    @Builder.Default
    double powerCellMax=MAX_POWER_CELL;
    @Builder.Default
    double heatCapacityCoefficient=HEAT_CAPACITY_DEFAULT;
    @Builder.Default
    double heatTransferCoefficient=HEAT_TRANSFER_DEFAULT;
    @Builder.Default
    double heatArea=HEAT_AREA_DEFAULT;
    @Builder.Default
    double[] socValuesForOcvCurve = SOC_VALUES;
    @Builder.Default
    double[] ocvValuesForOcvCurve = OCV_VALUES;
    @Builder.Default
    double dt=DT_DEFAULT;
    @Builder.Default
    double temperatureAmbient=TEMP_AMB_DEFAULT;
    @Builder.Default
    double maxTime=MAX_TIME_DEFAULT;
}
