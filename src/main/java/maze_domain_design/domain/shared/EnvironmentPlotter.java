package maze_domain_design.domain.shared;

import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import lombok.AllArgsConstructor;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.services.PlottingSettings;

import javax.swing.*;
import java.io.IOException;

import static common.other.Conditionals.executeIfTrue;

@AllArgsConstructor
public class EnvironmentPlotter {

    Environment environment;
    PlottingSettings settings;

    public void plot() {
        var e = new GridSizeExtractor(environment.getProperties());
        var table = createTable(e);
        showTable(e, table);
    }

    public void savePlot(String dir, String fileName, String fileEnd) throws IOException {
        var e = new GridSizeExtractor(environment.getProperties());
        var tableShower = createTableShower(e);
        var tableDataValues = TableDataString.ofMat(createTable(e));
        var frame=tableShower.createTableFrame(tableDataValues);
        tableShower.saveTableFrame(frame,dir,fileName+fileEnd);
    }

    String[][] createTable(GridSizeExtractor e) {
        String[][] values = new String[e.nX()][e.nY()];
        var ep = environment.getProperties();
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                StateRoad state = StateRoad.of(x, y, ep);
                StringBuilder sb = new StringBuilder();
                executeIfTrue(state.isTerminal(), () -> sb.append("T"));
                executeIfTrue(state.isFail(ep), () -> sb.append("F"));
                values[x][y] = sb.toString();
            }
        }
        return values;
    }


    void showTable(GridSizeExtractor e, String[][] table) {
        TableShower tableShower = createTableShower(e);
        var tableDataValues = TableDataString.ofMat(table);
        SwingUtilities.invokeLater(() -> tableShower.showTable(tableDataValues));
    }

     TableShower createTableShower(GridSizeExtractor e) {
        var settingsValues = TableSettings.ofNxNy(e.nX(), e.nY())
                .withName("Environment").withFormatTicks("%.0f");
        return new TableShower(settingsValues);
    }

}
