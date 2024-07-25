package domain_design_tabular_q_learning.environments.avoid_obstacle;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.plotting.Agent2dMemPlotterHelper;
import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.domain.plotting.GridSizeInformer;
import domain_design_tabular_q_learning.services.PlottingSettings;
import domain_design_tabular_q_learning.domain.plotting.Tables;
import java.io.IOException;

/**
 * Needs to be specific for environment due to
 *  var state = (StateI<V>) StateRoad.of(x, y, ep)
 *  Agent2dMemPlotterHelper does the heavy/general work
 */

public class RoadAgentPlotter<V,A> {
    public final Agent<V,A> agent;
    EnvironmentI<V,A> environment;
    public final PlottingSettings settings;
    GridSizeInformer gridInfo;
    Agent2dMemPlotterHelper<V,A> helper;

    public RoadAgentPlotter(Agent<V, A> agent,
                            EnvironmentI<V,A> environment,
                            PlottingSettings settings) {
        this.agent=agent;
        this.environment=environment;
        this.settings=settings;
        this.gridInfo = new GridSizeInformer(environment.getProperties());
        this.helper=new Agent2dMemPlotterHelper<>(agent,environment,settings, gridInfo);
    }

    public void plot() {
        helper.showTables(createTables());
    }

    public void saveCharts(FileDirName file) throws IOException {
        helper.saveCharts(createTables(),file);
    }

    Tables createTables() {
        var ep=environment.getProperties();
        var e=gridInfo;
        var tables = new Tables(e);
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                var state = (StateI<V>) StateRoad.of(x, y, ep);
                if(!state.isTerminal()) {
                        helper.fillTablesFromState(tables, y, x, state);
                }
            }
        }
        return tables;
    }



}
