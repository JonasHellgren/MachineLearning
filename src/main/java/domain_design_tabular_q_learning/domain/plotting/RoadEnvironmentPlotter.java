package domain_design_tabular_q_learning.domain.plotting;

import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.environments.obstacle_on_road.GridVariables;
import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.environments.obstacle_on_road.EnvironmentRoad;
import domain_design_tabular_q_learning.environments.obstacle_on_road.StateRoad;
import domain_design_tabular_q_learning.services.PlottingSettings;

import javax.swing.*;
import java.io.IOException;

import static common.other.Conditionals.executeIfTrue;

@AllArgsConstructor
public class RoadEnvironmentPlotter<V,A> {

    EnvironmentI<V,A> environment;
    PlottingSettings settings;

    public void plot() {
        var e = new GridSizeInformer(environment.getProperties());
        var table = createTable(e);
        showTable(e, table);
    }

    public void savePlot(FileDirName file) throws IOException {
        var e = new GridSizeInformer(environment.getProperties());
        var tableShower = createTableShower(e);
        var tableDataValues = TableDataString.ofMat(createTable(e));
        var frame=tableShower.createTableFrame(tableDataValues);
        tableShower.saveTableFrame(frame,file.dir(),file.fileName()+file.fileEnd());
    }

    String[][] createTable(GridSizeInformer e) {
        String[][] values = new String[e.nX()][e.nY()];
        var ep = environment.getProperties();
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                StateI<GridVariables> state = StateRoad.of(x, y, ep);
                StringBuilder sb = new StringBuilder();
                executeIfTrue(state.isTerminal(), () -> sb.append("T"));
                executeIfTrue(state.isFail(), () -> sb.append("F"));
                values[x][y] = sb.toString();
            }
        }
        return values;
    }


    void showTable(GridSizeInformer e, String[][] table) {
        TableShower tableShower = createTableShower(e);
        var tableDataValues = TableDataString.ofMat(table);
        SwingUtilities.invokeLater(() -> tableShower.showTable(tableDataValues));
    }

     TableShower createTableShower(GridSizeInformer e) {
        var settingsValues = TableSettings.ofNxNy(e.nX(), e.nY())
                .withName("Environment").withFormatTicks("%.0f");
        return new TableShower(settingsValues);
    }

}
