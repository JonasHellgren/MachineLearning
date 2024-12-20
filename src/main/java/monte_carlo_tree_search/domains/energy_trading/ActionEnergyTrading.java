package monte_carlo_tree_search.domains.energy_trading;

import common.other.RandUtilsML;
import monte_carlo_tree_search.interfaces.ActionIntegerAbstract;
import monte_carlo_tree_search.interfaces.ActionInterface;

public class ActionEnergyTrading extends ActionIntegerAbstract {

    public static final int MIN_ACTION_DEFAULT = -2;
    public static final int MAX_ACTION_DEFAULT = 2;

     ActionEnergyTrading(int value) {
       super(MIN_ACTION_DEFAULT,MAX_ACTION_DEFAULT,value);
    }

    public static ActionEnergyTrading newValue(int value) {
         return new ActionEnergyTrading(value);
    }


    public static ActionEnergyTrading newRandom() {
        return new ActionEnergyTrading(RandUtilsML.getRandomIntNumber(MIN_ACTION_DEFAULT,MAX_ACTION_DEFAULT+1));
    }


    public String toString() {
         return "value = "+getValue();
    }

    @Override
    public ActionInterface<Integer> copy() {
        return new ActionEnergyTrading(super.actionValue);
    }
}
