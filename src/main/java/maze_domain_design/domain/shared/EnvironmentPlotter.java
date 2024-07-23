package maze_domain_design.domain.shared;

import common.other.Conditionals;
import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import lombok.AllArgsConstructor;
import maze_domain_design.domain.environment.Environment;
import maze_domain_design.domain.environment.value_objects.State;
import maze_domain_design.services.PlottingSettings;

import javax.swing.*;

@AllArgsConstructor
public class EnvironmentPlotter {

    Environment environment;
    PlottingSettings settings;

    public void plot() {
        var e=new GridSizeExtractor(environment.getProperties());
        var table= createTable(e);
        showTable(e, table);
    }

    String[][] createTable(GridSizeExtractor e) {
        String[][] values=new String[e.nX()][e.nY()];
        var ep = environment.getProperties();
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                State state = State.of(x, y, ep);
                StringBuilder sb = new StringBuilder();
                Conditionals.executeIfTrue(state.isTerminal(), () -> sb.append("T"));
                Conditionals.executeIfTrue(state.isFail(ep), () -> sb.append("F"));
                values[x][y]=sb.toString();
            }
        }
        return values;
    }


    private static void showTable(GridSizeExtractor e, String[][] tables) {
        var settingsValues = TableSettings.ofNxNy(e.nX(), e.nY()).withName("State properties");
        var tableDataValues = TableDataString.ofMat(tables);
        var tableShower1 = new TableShower(settingsValues);
        SwingUtilities.invokeLater(() -> tableShower1.showTable(tableDataValues));
    }

}
