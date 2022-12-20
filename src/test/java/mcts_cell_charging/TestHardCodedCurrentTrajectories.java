package mcts_cell_charging;

import monte_carlo_tree_search.classes.StepReturnGeneric;
import monte_carlo_tree_search.domains.models_battery_cell.*;
import monte_carlo_tree_search.generic_interfaces.EnvironmentGenericInterface;
import org.apache.arrow.flatbuf.Int;
import org.jcodec.common.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestHardCodedCurrentTrajectories {


    private static final double DELTA = 0.01;
    private static final int MAX_RELATIVE_CURRENT = 1;
    private static final double MIN_RELATIVE_CURRENT = 0.5;
    private static final double SOC_INIT = 0.1;
    private static final int TEMPERATURE_INIT = 20;
    private static final int TIME_INIT = 0;
    private static final int DT = 10;
    private static final int NOF_CURRENT_LEVELS = 5;
    private static final int MAX_TIME = 240;
    EnvironmentGenericInterface<CellVariables, Integer> environment;
    StateCell state;
    CellSettings cellSettings;
    CellVariables variables;
    ActionCell action;
    Integer maxCurrentLevel=NOF_CURRENT_LEVELS-1;

    @Before
    public void init() {
        cellSettings=CellSettings.builder()
                .maxTime(MAX_TIME)
                .dt(DT).build();
        environment=new EnvironmentCell(cellSettings);
        state= new StateCell(CellVariables.builder()
                .time(TIME_INIT).temperature(TEMPERATURE_INIT).SoC(SOC_INIT).build());
        variables=state.getVariables();
        action= ActionCell.builder()
                .minRelativeCurrent(MIN_RELATIVE_CURRENT)
                .maxRelativeCurrent(MAX_RELATIVE_CURRENT)
                .nofCurrentLevels(NOF_CURRENT_LEVELS).build();
    }

    @Test
    public void maxCurrentTrajectoryGivesToHighVoltage() {
        List<Integer> currentTraj=  Collections.nCopies(MAX_TIME/DT, maxCurrentLevel);
        List<EnvironmentCell.CellResults> resultsList=simulate(currentTraj);
        resultsList.forEach(System.out::println);

        Assert.assertTrue(resultsList.size()<currentTraj.size());
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getVoltage()>cellSettings.getMaxVoltage());
    }

    @Test
    public void highCurrentTrajectoryGivesToHighTemp() {
        List<Integer> currentTraj=  Collections.nCopies(MAX_TIME/DT, maxCurrentLevel-2);
        List<EnvironmentCell.CellResults> resultsList=simulate(currentTraj);
        resultsList.forEach(System.out::println);

        Assert.assertTrue(resultsList.size()<currentTraj.size());
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getNewTemperature()>cellSettings.getMaxTemperature());
    }

    @Test
    public void moderateCurrentTrajectoryGivesNoViolation() {
        List<Integer> currentTraj=  Collections.nCopies(MAX_TIME/DT, maxCurrentLevel-4);
        List<EnvironmentCell.CellResults> resultsList=simulate(currentTraj);
        resultsList.forEach(System.out::println);

        Assert.assertTrue(resultsList.size()==currentTraj.size());
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getVoltage()<cellSettings.getMaxVoltage());
        Assert.assertTrue(resultsList.get(resultsList.size()-1).getNewTemperature()<cellSettings.getMaxTemperature());
    }

    private List<EnvironmentCell.CellResults> simulate(List<Integer> currentTraj) {
        List<EnvironmentCell.CellResults> resultsList=new ArrayList<>();
        for (Integer current: currentTraj) {
            action.setValue(current);
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
