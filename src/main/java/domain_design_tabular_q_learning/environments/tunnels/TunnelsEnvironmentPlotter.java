package domain_design_tabular_q_learning.environments.tunnels;

import common.plotters.table_shower.TableDataString;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.helpers.GridInformerI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.plotting.Environment2dPlotterI;
import domain_design_tabular_q_learning.domain.plotting.EnvironmentPlotterHelper;
import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadGridInformer;
import domain_design_tabular_q_learning.environments.avoid_obstacle.StateRoad;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import domain_design_tabular_q_learning.services.PlottingSettings;
import lombok.AllArgsConstructor;

import java.io.IOException;

import static common.other.Conditionals.executeIfTrue;

@AllArgsConstructor
public class TunnelsEnvironmentPlotter<V,A,P> implements Environment2dPlotterI {

    EnvironmentI<V,A,P> environment;
    PlottingSettings settings;
    EnvironmentPlotterHelper helper;

    public TunnelsEnvironmentPlotter(EnvironmentI<V, A, P> environment, PlottingSettings settings) {
        this(environment,settings,new EnvironmentPlotterHelper());
    }

    @Override
    public void plot() {
        var e = new TunnelGridInformer((PropertiesTunnels) environment.getProperties());
        var table = createTable(e);
        helper.showTunnelsTable(e, table);
    }

    @Override
    public void savePlot(FileDirName file) throws IOException {
        var e = new TunnelGridInformer((PropertiesTunnels) environment.getProperties());
        var tableShower = helper.createTableShower(e);
        var tableDataValues = TableDataString.ofMat(createTable(e));
        var frame=tableShower.createTableFrame(tableDataValues);
        tableShower.saveTableFrame(frame,file.dir(),file.fileName()+file.fileEnd());
    }

    String[][] createTable(TunnelGridInformer e) {
        String[][] values = new String[e.nX()][e.nY()];
        var ep = environment.getProperties();
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                PropertiesTunnels pt = (PropertiesTunnels) ep;
                XyPos pos = XyPos.of(x, y);
                boolean isBlocked= pt.blockedPositions().contains(pos);
                boolean isTermNonFail=pt.isTerminalNonFail(pos);
                boolean isTermFail=pt.isTermFail(pos);
                double rewardTermNonFail=pt.rewardOfTerminalNonFail(pos).orElse(0d);
                double rewardTermFail=pt.rewardOfFail(pos).orElse(0d);
                StringBuilder sb = new StringBuilder();
                executeIfTrue(isTermNonFail, () -> sb.append("T:").append(rewardTermNonFail));
                executeIfTrue(isTermFail, () -> sb.append("TF:").append(rewardTermFail));
                executeIfTrue(isBlocked, () -> sb.append("######"));
                values[x][y] = sb.toString();
            }
        }
        return values;
    }


}
