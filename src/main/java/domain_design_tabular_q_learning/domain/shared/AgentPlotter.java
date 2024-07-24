package domain_design_tabular_q_learning.domain.shared;

import common.other.Conditionals;
import common.plotters.table_shower.TableDataI;
import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.domain.environment.value_objects.Action;
import domain_design_tabular_q_learning.environments.obstacle_on_road.StateRoad;
import domain_design_tabular_q_learning.services.PlottingSettings;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    EnvironmentRoad environment;
    PlottingSettings settings;

    public void plot() {
        var nXnY = new GridSizeExtractor(environment.getProperties());
        Tables tables = createTables(nXnY);
        showTables(nXnY, tables);
    }


    public void saveCharts(String dir, String fileName, String fileEnd) throws IOException {
        var nXnY = new GridSizeExtractor(environment.getProperties());
        Tables tables = createTables(nXnY);
        var ssMap = getShowerSettingsMap(nXnY, tables);
        for(Map.Entry<TableShower, TableDataI> e: ssMap.entrySet()) {
            var frame = e.getKey().createTableFrame(e.getValue());
            e.getKey().saveTableFrame(frame, dir, fileName + e.getKey().getSettings().name() + fileEnd);
        };

    }

    Tables createTables(GridSizeExtractor e) {
        var ep = environment.getProperties();
        var tables = new Tables(e);

        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                StateRoad state = StateRoad.of(x, y, ep);
                int finalY = y;
                int finalX = x;
                Conditionals.executeIfFalse(state.isTerminal(), () ->
                        fillTablesFromState(tables, finalY, finalX, state));
            }
        }
        return tables;
    }

    void fillTablesFromState(Tables tables, int y, int x, StateRoad state) {
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
        var ssMap = getShowerSettingsMap(nXnY, tables);
        ssMap.forEach((key, value) -> SwingUtilities.invokeLater(() -> key.showTable(value)));
    }

    @NotNull
    private Map<TableShower, TableDataI> getShowerSettingsMap(GridSizeExtractor nXnY, Tables tables) {
        Map<TableShower, TableDataI> showerSettingsMap=new HashMap<>();
        var settingsValues = TableSettings.ofNxNy(nXnY.nX(), nXnY.nY())
                .withName("State values").withFormatTicks("%.0f");
        var settingsActions = TableSettings.ofNxNy(nXnY.nX(), nXnY.nY())
                .withName("Actions").withFormatTicks("%.0f");
        var settingsStateActionValues = TableSettings.ofNxNy(nXnY.nX(), nXnY.nY())
                .withName("State action values").withFormatTicks("%.0f")
                .withMaxCharsPerCell(settings.maxCharsPerStateActionCell());
        var tableDataValues = TableDataString.ofMat(tables.values);
        var tableDataActions = TableDataString.ofMat(tables.actions);
        var tableDataSaValues = TableDataString.ofMat(tables.stateActionValues);
        showerSettingsMap.put(new TableShower(settingsValues),tableDataValues);
        showerSettingsMap.put(new TableShower(settingsActions),tableDataActions);
        showerSettingsMap.put(new TableShower(settingsStateActionValues),tableDataSaValues);
        return showerSettingsMap;
    }


}
