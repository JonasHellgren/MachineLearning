package monte_carlo_tree_search.domains.energy_trading;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.StateInterface;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EnvironmentEnergyTrading implements EnvironmentGenericInterface<VariablesEnergyTrading, Integer> {


    public static final int MIN_TIME = 0;
    public static final int MAX_TIME = 7;
    public static final int TIME_STEP_IN_HOUR_DURATION = 3;


    private static final List<Double> PRICE_DEFAULT= Arrays.asList(0.5,1d,1d,1d,1d,1d,1.5,1d);
    private static final Map<Integer, Double> ACTION_POWER_IN_KW_MAP = Map.of(
            -2,-2d, -1,-1d,0,-0d, 1,1d,2,2d);
    private static final Double BATTERY_ENERGY=30d;  //kWh

    private List<Double> priceVsTime;

    public EnvironmentEnergyTrading(List<Double> priceVsTime) {
        this.priceVsTime = priceVsTime;
    }

    public static EnvironmentEnergyTrading newDefault() {
        return new EnvironmentEnergyTrading(PRICE_DEFAULT);
    }

    public static EnvironmentEnergyTrading newFromPrices(List<Double> priceVsTime) {
        return new EnvironmentEnergyTrading(priceVsTime);
    }


    @Override
    public StepReturnGeneric<VariablesEnergyTrading> step(ActionInterface<Integer> action,
                                                          StateInterface<VariablesEnergyTrading> state) {


        int timePres=state.getVariables().time;
        double pricePres=priceVsTime.get(timePres);
        double soEPres=state.getVariables().SoE;

        double presentPower= ACTION_POWER_IN_KW_MAP.get(action.getValue());

        int timeNew=timePres+1;
        double soENew= soEPres+presentPower*(double) TIME_STEP_IN_HOUR_DURATION/BATTERY_ENERGY;


        return null;
    }
}
