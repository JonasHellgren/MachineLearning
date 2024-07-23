package maze_domain_design.domain.shared;

import common.plotters.SwingShow2DArray;
import common.plotters.table_shower.TableDataDouble;
import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import lombok.AllArgsConstructor;
import maze_domain_design.domain.agent.Agent;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.State;

import javax.swing.*;
import java.util.Optional;

@AllArgsConstructor
public class AgentPlotter {

    Agent agent;
    Environment environment;

    public void plot() {
        var ep = environment.getProperties();
        int nX = ep.minMaxX().getSecond() - ep.minMaxX().getFirst() + 1;
        int nY = ep.minMaxY().getSecond() - ep.minMaxY().getFirst() + 1;
        Double[][] values=new Double[nX][nY];
        String[][] actions=new String[nX][nY];
        int minY = ep.minMaxY().getFirst();
        int maxY = ep.minMaxY().getSecond();
        int minX = ep.minMaxX().getFirst();
        int maxX = ep.minMaxX().getSecond();

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                State state = State.of(x, y, ep);
                if (!state.isTerminal()) {
                    values[x][y] = agent.getMemory().valueOfState(state);
                    actions[x][y] = agent.chooseAction(state, 0d).arrow;
                } else {
                    values[x][y] = 0d;
                }
            }
        }

        var settingsValues = TableSettings.ofNxNy(nX, nY).withName("Values");
        var settingsActions = TableSettings.ofNxNy(nX, nY).withName("Actions");
        var tableDataValues = TableDataDouble.ofMatAndSettings(values, settingsValues);
        var tableDataActions = TableDataString.ofMat(actions);
        var tableShower1 = new TableShower(settingsValues);
        var tableShower2 = new TableShower(settingsActions);

        SwingUtilities.invokeLater(() -> tableShower1.showTable(tableDataValues));
        SwingUtilities.invokeLater(() -> tableShower2.showTable(tableDataActions));


    }




}
