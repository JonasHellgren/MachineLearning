package domain_design_tabular_q_learning.domain.plotting;

import common.plotters.table_shower.TableDataString;
import common.plotters.table_shower.TableSettings;
import common.plotters.table_shower.TableShower;
import domain_design_tabular_q_learning.domain.environment.helpers.GridInformerI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadGridInformer;
import domain_design_tabular_q_learning.environments.tunnels.TunnelGridInformer;

import javax.swing.*;

public class EnvironmentPlotterHelper {


    public TableShower createTableShower(GridInformerI<PropertiesRoad> e) {
        var settingsValues = TableSettings.ofNxNy(e.nX(), e.nY())
                .withName("Environment").withFormatTicks("%.0f")
                ;
        return new TableShower(settingsValues);
    }

    //fix duplication with GridInformerI
    public void showRoadTable(RoadGridInformer e, String[][] table) {
        TableShower tableShower = createTableShower(e);
        var tableDataValues = TableDataString.ofMat(table);
        SwingUtilities.invokeLater(() -> tableShower.showTable(tableDataValues));
    }

    public void showTunnelsTable(TunnelGridInformer e, String[][] table) {
        TableShower tableShower = createTableShower(e);
        var tableDataValues = TableDataString.ofMat(table);
        SwingUtilities.invokeLater(() -> tableShower.showTable(tableDataValues));
    }

}
