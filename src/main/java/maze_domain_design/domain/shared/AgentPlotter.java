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

    //inner class to ease data transfer between methods
    record Tables(
            String[][] values,
            String[][] stateActionValues,
            String[][] actions
    ) {
        public Tables(GridSizeExtractor e) {
            this(new String[e.nX()][e.nY()], new String[e.nX()][e.nY()], new String[e.nX()][e.nY()]);
        }
    }

    Agent agent;
        Environment environment;
        PlottingSettings settings;

        public void plot() {
        var nXnY=new GridSizeExtractor(environment.getProperties());
        Tables tables = createTables(nXnY);
        showTables(nXnY, tables);
    }

    Tables createTables(GridSizeExtractor e) {
        var ep = environment.getProperties();
        var tables = new Tables(e);

        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                State state = State.of(x, y, ep);
                int finalY = y;
                int finalX = x;
                Conditionals.executeIfFalse(state.isTerminal(), () ->
                    fillTablesFromState(tables, finalY, finalX, state));
                }
        }
        return tables;
    }

    void fillTablesFromState(Tables tables, int y, int x, State state) {
        int nActions = agent.getProperties().actions().length;
        tables.values[x][y] = String.format(
                settings.tableCellFormatValues(),
                agent.getMemory().valueOfState(state));
        tables.actions[x][y] = agent.chooseAction(state, 0d).arrow;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Action[] actionArr = agent.getProperties().actions();
        var aEnd = actionArr[nActions - 1];
        for (Action a : actionArr) {
            StateAction sa = StateAction.of(state, a);
            var txt = String.format(settings.tableCellFormatActionValues()
                    , agent.getMemory().read(sa));
            sb.append(txt);
            Conditionals.executeIfFalse(a.equals(aEnd), () -> sb.append(";"));
        }
        sb.append("]");
        tables.stateActionValues[x][y] = sb.toString();
    }


    void showTables(GridSizeExtractor nXnY, Tables tables) {
        var settingsValues = TableSettings.ofNxNy(nXnY.nX(), nXnY.nY()).withName("State values");
        var settingsActions = TableSettings.ofNxNy(nXnY.nX(), nXnY.nY()).withName("Actions");
        var settingsStateActionValues = TableSettings.ofNxNy(nXnY.nX(), nXnY.nY())
                .withName("State action values").withMaxCharsPerCell(settings.maxCharsPerStateActionCell());
        var tableDataValues = TableDataString.ofMat(tables.values);
        var tableDataActions = TableDataString.ofMat(tables.actions);
        var tableDataSaValues = TableDataString.ofMat(tables.stateActionValues);
        var tableShower1 = new TableShower(settingsValues);
        var tableShower2 = new TableShower(settingsActions);
        var tableShower3 = new TableShower(settingsStateActionValues);

        SwingUtilities.invokeLater(() -> tableShower1.showTable(tableDataValues));
        SwingUtilities.invokeLater(() -> tableShower2.showTable(tableDataActions));
        SwingUtilities.invokeLater(() -> tableShower3.showTable(tableDataSaValues));
    }


}
