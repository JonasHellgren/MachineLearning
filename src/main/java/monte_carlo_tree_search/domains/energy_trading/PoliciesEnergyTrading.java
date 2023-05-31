package monte_carlo_tree_search.domains.energy_trading;

import monte_carlo_tree_search.interfaces.SimulationPolicyInterface;

public class PoliciesEnergyTrading {

    public static SimulationPolicyInterface<VariablesEnergyTrading, Integer> newRandom() {
        return new PolicyRandom();
    }

}
