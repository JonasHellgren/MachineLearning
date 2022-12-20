package mcts_classes;

import monte_carlo_tree_search.domains.models_space.*;
import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class TestSimulationPolicy {

    private static final double DELTA = 0.1;
    SpaceGrid spaceGrid;
    EnvironmentShip environment;

    @Before
    public void init() {
        spaceGrid= SpaceGridInterface.new3times7Grid();
        environment=new EnvironmentShip(spaceGrid);
    }

    @Test public void repeatingEqualActionProbManyTimes() {
        SimulationPolicyInterface<ShipVariables, ShipActionSet> policy= ShipPolicies.newEqualProbability();
        StateShip pos=StateShip.newStateFromXY(1,1);
        List<ShipActionSet> actionValueList=new ArrayList<>();
        for (int i = 0; i < 100 ; i++) {
            ActionInterface<ShipActionSet> action=policy.chooseAction(pos.copy());
            actionValueList.add(action.getValue());
        }

        System.out.println("actionList = " + actionValueList);

        Assert.assertTrue(actionValueList.contains(ShipActionSet.up));
        Assert.assertTrue(actionValueList.contains(ShipActionSet.still));
        Assert.assertTrue(actionValueList.contains(ShipActionSet.down));
        Assert.assertFalse(actionValueList.contains(ShipActionSet.notApplicable));

    }

    @Test
    public void moveWithMostStillSimulationPolicyGivesMostlyBetterSumRewardsFromUpperPositionThenMiddle() {
        SimulationPolicyInterface<ShipVariables, ShipActionSet> policy=ShipPolicies.newMostlyStill();

        System.out.println("environment = " + environment);

        StateShip pos=StateShip.newStateFromXY(1,1);
        List<Double> simResultsFromY1=getSumOfRewardsListFromMultipleSimulations(policy, pos);

        pos=StateShip.newStateFromXY(1,2);
        List<Double> simResultsFromY2=getSumOfRewardsListFromMultipleSimulations(policy, pos);

        System.out.println("simResultsFromY1 = " + simResultsFromY1);
        System.out.println("simResultsFromY2 = " + simResultsFromY2);

        OptionalDouble avg1=simResultsFromY1.stream().mapToDouble(d -> d).average();
        OptionalDouble avg2=simResultsFromY2.stream().mapToDouble(d -> d).average();

        Assert.assertTrue(avg1.orElseThrow()<avg2.orElseThrow());
    }

    private List<Double>  getSumOfRewardsListFromMultipleSimulations(
            SimulationPolicyInterface<ShipVariables,
            ShipActionSet> policy, StateShip pos) {
        List<Double> sumOfRewardsList=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            List<StepReturnGeneric<ShipVariables>> returns = stepToTerminal(pos.copy(), policy);
            double sumOfRewards=returns.stream().mapToDouble(r -> r.reward).sum();
             sumOfRewardsList.add(sumOfRewards);
        }
        return sumOfRewardsList;
    }

    @NotNull
    private List<StepReturnGeneric<ShipVariables>> stepToTerminal(StateShip pos,
                                                                  SimulationPolicyInterface<ShipVariables, ShipActionSet> policy) {
        List<StepReturnGeneric<ShipVariables>> returns=new ArrayList<>();
        StepReturnGeneric<ShipVariables> stepReturn;
        do {
            ActionInterface<ShipActionSet> action=policy.chooseAction(pos);
            stepReturn = environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            returns.add(stepReturn);

        } while (!stepReturn.isTerminal);
        return returns;
    }

}
