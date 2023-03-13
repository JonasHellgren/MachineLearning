package monte_carlo_tree_search.domains.cart_pole;

import common.ListUtils;
import common.MultiplePanelsPlotter;
import lombok.SneakyThrows;
import monte_carlo_tree_search.create_tree.MonteCarloTreeCreator;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.classes.TreePlotData;
import monte_carlo_tree_search.interfaces.ActionInterface;
import monte_carlo_tree_search.interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.interfaces.NetworkMemoryInterface;
import monte_carlo_tree_search.interfaces.StateInterface;
import monte_carlo_tree_search.swing.CartPoleGraphics;

import java.util.Arrays;

/**
 * Used to run cart pole environment using MCTS and pre-defined memory.
 */

public class CartPoleRunner {

    private static final String TITLE = "CartPole evaluation animation";
    MonteCarloTreeCreator<CartPoleVariables, Integer> mcForSearch;
    EnvironmentGenericInterface<CartPoleVariables, Integer> environmentNotStepLimited;
    CartPoleGraphics graphics;
    NetworkMemoryInterface<CartPoleVariables,Integer> memory;
    int nofSteps;
    MultiplePanelsPlotter plotter;


    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,Integer> mcForSearch,
                          int nofSteps) {
        this(mcForSearch,new CartPoleStateValueMemory<>(),nofSteps,null);
    }

    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,Integer> mcForSearch,
                          int nofSteps,
                          MultiplePanelsPlotter plotter) {
        this(mcForSearch,new CartPoleStateValueMemory<>(),nofSteps,plotter);
    }

    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,Integer> mcForSearch,
                          NetworkMemoryInterface<CartPoleVariables,Integer> memory,
                          int nofSteps) {
        this(mcForSearch,memory,nofSteps,null);
    }


    public CartPoleRunner(MonteCarloTreeCreator<CartPoleVariables,Integer> mcForSearch,
                          NetworkMemoryInterface<CartPoleVariables,Integer> memory,
                          int nofSteps,
                          MultiplePanelsPlotter plotter) {
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

            doPlotting();
            ActionInterface<Integer> actionCartPole = mcForSearch.getFirstAction();
            StepReturnGeneric<CartPoleVariables> sr = environmentNotStepLimited.step(actionCartPole, state);
            state.setFromReturn(sr);
            double value = memory.read(state);
            double rootNodeValue= ListUtils.findEnd(mcForSearch.getPlotData()).orElse(TreePlotData.builder().build()).maxValue;
            graphics.render(state, i, value,rootNodeValue, actionCartPole.getValue());
            isFail = sr.isFail;
            i++;
        } while (i < nofSteps && !isFail);
    }


    private void doPlotting() throws NoSuchFieldException {
        if (plotter!=null) {

            plotter.plot(Arrays.asList(
                    ListUtils.getListOfField(mcForSearch.getPlotData(),"maxValue"),
                    ListUtils.getListOfField(mcForSearch.getPlotData(),"nofNodes"),
                    ListUtils.getListOfField(mcForSearch.getPlotData(),"maxDepth")
            ));
        }
    }

}
