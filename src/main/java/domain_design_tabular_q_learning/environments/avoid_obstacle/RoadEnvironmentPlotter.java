package domain_design_tabular_q_learning.environments.avoid_obstacle;

import common.plotters.table_shower.TableDataString;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.helpers.GridInformerI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.plotting.Environment2dPlotterI;
import domain_design_tabular_q_learning.domain.plotting.EnvironmentPlotterHelper;
import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.environments.shared.XyPos;
import lombok.AllArgsConstructor;
import domain_design_tabular_q_learning.services.PlottingSettings;
import java.io.IOException;

import static common.other.Conditionals.executeIfTrue;

@AllArgsConstructor
public class RoadEnvironmentPlotter<V,A,P> implements Environment2dPlotterI {

    EnvironmentI<V,A,P> environment;
    PlottingSettings settings;
    EnvironmentPlotterHelper helper;

    public RoadEnvironmentPlotter(EnvironmentI<V, A, P> environment, PlottingSettings settings) {
        this(environment,settings,new EnvironmentPlotterHelper());
    }

    @Override
    public void plot() {
        var e = new RoadGridInformer((PropertiesRoad) environment.getProperties());
        var table = createTable(e);
        helper.showTable(e, table);
    }

    @Override
    public void savePlot(FileDirName file) throws IOException {
        var e = new RoadGridInformer((PropertiesRoad) environment.getProperties());
        var tableShower = helper.createTableShower(e);
        var tableDataValues = TableDataString.ofMat(createTable(e));
        var frame=tableShower.createTableFrame(tableDataValues);
        tableShower.saveTableFrame(frame,file.dir(),file.fileName()+file.fileEnd());
    }

    String[][] createTable(GridInformerI<PropertiesRoad> e) {
        String[][] values = new String[e.nX()][e.nY()];
        var ep = environment.getProperties();
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                StateI<XyPos> state = StateRoad.of(x, y, (PropertiesRoad) ep);
                StringBuilder sb = new StringBuilder();
                executeIfTrue(state.isTerminal(), () -> sb.append("T"));
                executeIfTrue(state.isFail(), () -> sb.append("F"));
                values[x][y] = sb.toString();
            }
        }
        return values;
    }


}
