package monte_carlo_tree_search.domains.cart_pole;

import common.ListUtils;
import lombok.SneakyThrows;
import monte_carlo_tree_search.classes.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.CartPoleStateValueMemory;
import monte_carlo_tree_search.swing.CartPoleGraphics;

import java.util.ArrayList;

/**
 * Used to run cart pole environment using MCTS and pre-defined memory.
 */

public class CartPoleRunner {

    private static final String TITLE = "CartPole evaluation animation";
    MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environmentNotStepLimited;
    CartPoleGraphics graphics;
    NetworkMemoryInterface<CartPoleVariables> memory;
    int nofSteps;
    TwoPanelsPlotter plotter;


    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,
                          Integer> mcForSearch,
                          int nofSteps) {
        this(mcForSearch,new CartPoleStateValueMemory<>(),nofSteps,null);
    }

    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,
            Integer> mcForSearch,
                          NetworkMemoryInterface<CartPoleVariables> memory,
                          int nofSteps) {
        this(mcForSearch,memory,nofSteps,null);
    }


    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,Integer> mcForSearch,
                          NetworkMemoryInterface<CartPoleVariables> memory,
                          int nofSteps,
                          TwoPanelsPlotter plotter) {
        this.mcForSearch = mcForSearch;
        this.memory=memory;
        this.nofSteps = nofSteps;
        environmentNotStepLimited= EnvironmentCartPole.builder().maxNofSteps(Integer.MAX_VALUE).build();
        graphics=new CartPoleGraphics(TITLE);
        this.plotter=plotter;
    }

    @SneakyThrows
    public void run(StateInterface<CartPoleVariables> state) {
        int i = 0;
        boolean isFail;

        do {
            state.getVariables().nofSteps = 0;  //reset nof steps
            mcForSearch.setStartState(state);
            mcForSearch.run();

            if (plotter!=null) {
                plotter.plot(mcForSearch.getRootValueList(), new ArrayList<>());
            }
            ActionInterface<Integer> actionCartPole = mcForSearch.getFirstAction();
            StepReturnGeneric<CartPoleVariables> sr = environmentNotStepLimited.step(actionCartPole, state);
            state.setFromReturn(sr);
            double value = memory.read(state);
            double rootNodeValue= ListUtils.findEnd(mcForSearch.getRootValueList());
            graphics.render(state, i, value,rootNodeValue, actionCartPole.getValue());
            isFail = sr.isFail;
            i++;
        } while (i < nofSteps && !isFail);
    }

}
