package domain_design_tabular_q_learning.environments.obstacle_on_road;

import common.other.Conditionals;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.shared.AgentPlotter;
import domain_design_tabular_q_learning.domain.shared.GridSizeInformer;
import domain_design_tabular_q_learning.services.PlottingSettings;
import domain_design_tabular_q_learning.domain.shared.Tables;

public class RoadAgentPlotter<V,A> extends AgentPlotter<V,A> {

    EnvironmentRoad environment;

    public RoadAgentPlotter(Agent<V, A> agent,
                            EnvironmentRoad environment,
                            PlottingSettings settings) {
        super(agent, settings,new GridSizeInformer(environment.properties));
        this.environment=environment;
    }

    public Tables createTables() {
        var ep=environment.properties;
        var e=super.gridInfo;
        var tables = new Tables(e);

        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
                var state = (StateI<V>) StateRoad.of(x, y, ep);
              //  var state =  StateRoad.of(x, y, ep);
                int finalY = y;
                int finalX = x;
                Conditionals.executeIfFalse(state.isTerminal(), () ->
                        fillTablesFromState(tables, finalY, finalX, state));
            }
        }
        return tables;
    }

    void fillTablesFromState(Tables tables, int y, int x, StateI<V> state) {
         ActionRoad[] actionArr = environment.actions();
        int nActions = actionArr.length;
        tables.values()[x][y] = String.format(
                settings.tableCellFormatValues(),
                agent.getMemory().valueOfState(state));
        tables.actions()[x][y] = agent.chooseAction(state, 0d).toString();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        ActionRoad aEnd = actionArr[nActions - 1];
        for (ActionRoad a : actionArr) {
            var sa = (StateAction<V, A>) StateAction.of(state, a);
            var txt = String.format(settings.tableCellFormatActionValues()
                    , agent.getMemory().read(sa));
            sb.append(txt);
            Conditionals.executeIfFalse(a.equals(aEnd), () -> sb.append(";"));
        }
        sb.append("]");
        tables.stateActionValues()[x][y] = sb.toString();
    }

}
