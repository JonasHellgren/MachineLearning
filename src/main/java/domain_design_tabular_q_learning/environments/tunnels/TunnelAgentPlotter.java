package domain_design_tabular_q_learning.environments.tunnels;

import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.helpers.GridInformerI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.plotting.Agent2dMemPlotterHelper;
import domain_design_tabular_q_learning.domain.plotting.FileDirName;
import domain_design_tabular_q_learning.domain.plotting.Tables;
import domain_design_tabular_q_learning.environments.avoid_obstacle.PropertiesRoad;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadGridInformer;
import domain_design_tabular_q_learning.environments.avoid_obstacle.StateRoad;
import domain_design_tabular_q_learning.services.PlottingSettings;

import java.io.IOException;

/**
 * Needs to be specific for environment due to
 *  var state = (StateI<V>) StateRoad.of(x, y, ep)
 *  Agent2dMemPlotterHelper does the heavy/general work
 */

public class TunnelAgentPlotter<V,A,P> {
    public final Agent<V,A,P> agent;
    EnvironmentI<V,A,P> environment;
    public final PlottingSettings settings;
    GridInformerI<P> gridInfo;
    Agent2dMemPlotterHelper<V,A,P> helper;

    public TunnelAgentPlotter(Agent<V, A,P> agent,
                              EnvironmentI<V,A,P> environment,
                              PlottingSettings settings) {
        this.agent=agent;
        this.environment=environment;
        this.settings=settings;
        this.gridInfo = (GridInformerI<P>) new RoadGridInformer(castProperties());
        this.helper=new Agent2dMemPlotterHelper<>(agent,environment,settings, gridInfo);
    }

    public void plot() {
        helper.showTables(createTables());
    }

    public void saveCharts(FileDirName file) throws IOException {
        helper.saveCharts(createTables(),file);
    }

    Tables createTables() {
        var e=gridInfo;
        var tables = new Tables(e);
        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                var state = (StateI<V>) StateRoad.ofXy(x, y,castProperties());
                if(!state.isTerminal()) {
                        helper.fillTablesFromState(tables, y, x, state);
                }
            }
        }
        return tables;
    }

    private PropertiesRoad castProperties() {
        return (PropertiesRoad) environment.getProperties();
    }


}
