package maze_domain_design.domain.shared;

import common.other.Conditionals;
import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import lombok.AllArgsConstructor;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.agent.value_objects.StateAction;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.services.PlottingSettings;

import javax.swing.*;

@AllArgsConstructor
public class AgentPlotter {

    Agent agent;
    Environment environment;
    PlottingSettings settings;

    public void plot() {
        var ep = environment.getProperties();
        int nX = ep.minMaxX().getSecond() - ep.minMaxX().getFirst() + 1;
        int nY = ep.minMaxY().getSecond() - ep.minMaxY().getFirst() + 1;
        int nActions = agent.getProperties().actions().length;

        String[][] values=new String[nX][nY];
        String[][] stateActionValues=new String[nX][nY];
        String[][] actions=new String[nX][nY];
        int minY = ep.minMaxY().getFirst();
        int maxY = ep.minMaxY().getSecond();
        int minX = ep.minMaxX().getFirst();
        int maxX = ep.minMaxX().getSecond();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                State state = State.of(x, y, ep);
                if (!state.isTerminal()) {
                    values[x][y] = String.format(
                            settings.tableCellFormatValues(),
                            agent.getMemory().valueOfState(state));
                    actions[x][y] = agent.chooseAction(state, 0d).arrow;

                    StringBuilder sb=new StringBuilder();
                    sb.append("[");
                    Action[] actionArr = agent.getProperties().actions();
                    var aEnd=actionArr[nActions-1];
                    for (Action a: actionArr) {
                        StateAction sa=StateAction.of(state,a);
                        var txt=String.format(settings.tableCellFormatActionValues()
                                ,agent.getMemory().read(sa));
                        sb.append(txt);
                        Conditionals.executeIfFalse(a.equals(aEnd), () -> sb.append(";"));
                    }
                    sb.append("]");
                    stateActionValues[x][y] = sb.toString();

                } else {
                    values[x][y] = "";
                }
            }
        }

        var settingsValues = TableSettings.ofNxNy(nX, nY).withName("State values");
        var settingsActions = TableSettings.ofNxNy(nX, nY).withName("Actions");
        var settingsStateActionValues = TableSettings.ofNxNy(nX, nY)
                .withName("State action values").withMaxCharsPerCell(settings.maxCharsPerStateActionCell());
        var tableDataValues = TableDataString.ofMat(values);
        var tableDataActions = TableDataString.ofMat(actions);
        var tableDataSaValues = TableDataString.ofMat(stateActionValues);
        var tableShower1 = new TableShower(settingsValues);
        var tableShower2 = new TableShower(settingsActions);
        var tableShower3 = new TableShower(settingsStateActionValues);

        SwingUtilities.invokeLater(() -> tableShower1.showTable(tableDataValues));
        SwingUtilities.invokeLater(() -> tableShower2.showTable(tableDataActions));
        SwingUtilities.invokeLater(() -> tableShower3.showTable(tableDataSaValues));


    }




}
