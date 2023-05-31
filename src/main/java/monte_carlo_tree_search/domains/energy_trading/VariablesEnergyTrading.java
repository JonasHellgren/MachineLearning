package monte_carlo_tree_search.domains.energy_trading;

import common.MathUtils;
import lombok.Builder;
import lombok.ToString;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;

import java.util.ArrayList;

@Builder
@ToString
public class VariablesEnergyTrading {

    public static final int DEFAULT_TIME = 0;
//    public static final double DEFAULT_PRICE = 1.0;
    public static final double DEFAULT_SOE = 0.5;
    private static final double DELTA = 0.01;

    @Builder.Default
    public int time= DEFAULT_TIME;
  //  @Builder.Default
 //   public double price= DEFAULT_PRICE;
    @Builder.Default
    public double SoE= DEFAULT_SOE;

    public static VariablesEnergyTrading newDefault() {
        return VariablesEnergyTrading.builder().build();
    }

    public VariablesEnergyTrading copy() {
        return VariablesEnergyTrading.builder()
                .time(time)
         //       .price(price)
                .SoE(SoE)
                .build();
    }

    @Override
    public boolean equals(Object obj) {

        //check if the argument is a reference to this object
        if (obj == this) return true;

        //check if the argument has the correct typ
        if (!(obj instanceof VariablesEnergyTrading)) return false;

        //For each significant field in the class, check if that field matches the corresponding field of this object
        VariablesEnergyTrading equalsSample = (VariablesEnergyTrading) obj;
        boolean isSameTime = this.time == equalsSample.time;
     //   boolean isSamePrice = MathUtils.compareDoubleScalars(this.price,equalsSample.price, DELTA);
        boolean isSameSoE = MathUtils.compareDoubleScalars(this.SoE,equalsSample.SoE, DELTA);

        return isSameTime && isSameSoE;
    }


}
