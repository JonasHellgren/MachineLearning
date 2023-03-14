package monte_carlo_tree_search.domains.energy_trading;

import monte_carlo_tree_search.interfaces.ActionIntegerAbstract;

public class ActionEnergyTrading extends ActionIntegerAbstract {

    public static final int MIN_ACTION_DEFAULT = -2;
    public static final int MAX_ACTION_DEFAULT = 2;

     ActionEnergyTrading(int value) {
       super(MIN_ACTION_DEFAULT,MAX_ACTION_DEFAULT,value);
    }

    public static ActionEnergyTrading newValue(int value) {
         return new ActionEnergyTrading(value);
    }

    public String toString() {
         return "value = "+getValue();
    }

}
