package mcts_spacegame;

import mcts_spacegame.enums.ShipAction;
import mcts_spacegame.environment.EnvironmentShip;
import mcts_spacegame.environment.StepReturnGeneric;
import mcts_spacegame.environment.StepReturnREMOVE;
import mcts_spacegame.models_space.ShipVariables;
import mcts_spacegame.models_space.SpaceGrid;
import mcts_spacegame.models_space.SpaceGridInterface;
import mcts_spacegame.models_space.StateShip;
import mcts_spacegame.policies_action.SimulationPolicyInterface;
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
        SimulationPolicyInterface policy=SimulationPolicyInterface.newEqualProbability();
        StateShip pos=StateShip.newStateFromXY(1,1);
        List<ShipAction> actionList=new ArrayList<>();
        for (int i = 0; i < 100 ; i++) {
            ShipAction action=policy.chooseAction(pos.copy());
            actionList.add(action);
        }

        System.out.println("actionList = " + actionList);

        Assert.assertTrue(actionList.contains(ShipAction.up));
        Assert.assertTrue(actionList.contains(ShipAction.still));
        Assert.assertTrue(actionList.contains(ShipAction.down));
        Assert.assertFalse(actionList.contains(ShipAction.notApplicable));

    }

    @Test
    public void moveWithMostStillSimulationPolicyGivesMostlyBetterSumRewardsFromUpperPositionThenMiddle() {
        SimulationPolicyInterface policy=SimulationPolicyInterface.newMostlyStill();

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

    private List<Double>  getSumOfRewardsListFromMultipleSimulations(SimulationPolicyInterface policy, StateShip pos) {
        List<Double> sumOfRewardsList=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            List<StepReturnGeneric<ShipVariables>> returns = stepToTerminal(pos.copy(), policy);
            double sumOfRewards=returns.stream().mapToDouble(r -> r.reward).sum();
             sumOfRewardsList.add(sumOfRewards);
        }
        return sumOfRewardsList;
    }

    @NotNull
    private List<StepReturnGeneric<ShipVariables>> stepToTerminal(StateShip pos, SimulationPolicyInterface policy) {
        List<StepReturnGeneric<ShipVariables>> returns=new ArrayList<>();
        StepReturnGeneric<ShipVariables> stepReturn;
        do {
            ShipAction action=policy.chooseAction(pos);
            stepReturn = environment.step(action, pos);
            pos.setFromReturn(stepReturn);
            returns.add(stepReturn);

        } while (!stepReturn.isTerminal);
        return returns;
    }

}
