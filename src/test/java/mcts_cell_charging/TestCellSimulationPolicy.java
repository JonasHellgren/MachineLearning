package mcts_cell_charging;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.models_battery_cell.*;
import monte_carlo_tree_search.generic_interfaces.ActionInterface;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import monte_carlo_tree_search.generic_interfaces.SimulationPolicyInterface;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestCellSimulationPolicy {

    private static final double SOC_INIT = 0.1;
    private static final int TEMPERATURE_INIT = 20;
    private static final int TIME_INIT = 0;
    private static final int DT = 10;
    private static final int NOF_CURRENT_LEVELS = 5;
    private static final int MAX_TIME = 120;
    EnvironmentGenericInterface<CellVariables, Integer> environment;
    StateCell state;
    CellSettings cellSettings;
    CellVariables variables;
    ActionInterface<Integer> action;
    Integer maxCurrentLevel=NOF_CURRENT_LEVELS-1;

    @Before
    public void init() {
        cellSettings=CellSettings.builder()
                .maxTime(MAX_TIME).dt(DT).build();
        environment=new EnvironmentCell(cellSettings);
        state= new StateCell(CellVariables.builder()
                .time(TIME_INIT).temperature(TEMPERATURE_INIT).SoC(SOC_INIT).build());
        variables=state.getVariables();
        action= ActionCell.builder()
                .nofCurrentLevels(NOF_CURRENT_LEVELS).build();

    }

    @Test
    public void simulateWithEqualProbEndsInToHighVoltage() {
        SimulationPolicyInterface<CellVariables, Integer> policy= CellPolicies.newEqualProbability(action);
        List<EnvironmentCell.CellResults> resultsList=simulate(policy);
        resultsList.forEach(System.out::println);
        Assert.assertTrue(resultsList.size()>0);
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getVoltage()>cellSettings.getMaxVoltage());
    }

    @Test
    public void simulateWithRandomFeasibleGivesNoViolation() {
        SimulationPolicyInterface<CellVariables, Integer> policy= CellPolicies.newRandomFeasible(action,environment);
        List<EnvironmentCell.CellResults> resultsList=simulate(policy);
        resultsList.forEach(System.out::println);
        Assert.assertTrue(resultsList.size()>0);
        AssertNoVoltageOrTempViolation(resultsList);
    }

    @Test
    public void simulateWithBestFeasibleGivesNoViolation() {
        SimulationPolicyInterface<CellVariables, Integer> policy= CellPolicies.newBestFeasible(action,environment);
        List<EnvironmentCell.CellResults> resultsList=simulate(policy);
        resultsList.forEach(System.out::println);
        Assert.assertTrue(resultsList.size()>0);
        AssertNoVoltageOrTempViolation(resultsList);
    }

    private void AssertNoVoltageOrTempViolation(List<EnvironmentCell.CellResults> resultsList) {
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getNewTemperature()<cellSettings.getMaxTemperature());
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getVoltage()<cellSettings.getMaxVoltage());
    }


    private List<EnvironmentCell.CellResults> simulate(SimulationPolicyInterface<CellVariables, Integer> policy) {
        List<EnvironmentCell.CellResults> resultsList=new ArrayList<>();
        for (int i = 0; i < MAX_TIME/DT; i++) {
            ActionInterface<Integer> action=policy.chooseAction(state.copy());
            StepReturnGeneric<CellVariables> sr=environment.step(action,state);
            state.setFromReturn(sr);
            EnvironmentCell environmentCasted= (EnvironmentCell) environment;
            resultsList.add(environmentCasted.getCellResults());
            if (sr.isTerminal) {
                break;
            }
        }
        return resultsList;
    }

}
