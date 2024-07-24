package domain_design_tabular_q_learning.environments.obstacle_on_road;

import common.other.Conditionals;
import domain_design_tabular_q_learning.domain.agent.Agent;
import domain_design_tabular_q_learning.domain.agent.value_objects.StateAction;
import domain_design_tabular_q_learning.domain.environment.EnvironmentI;
import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.domain.environment.value_objects.StateI;
import domain_design_tabular_q_learning.domain.shared.AgentPlotter;
import domain_design_tabular_q_learning.domain.shared.GridSizeExtractor;
import domain_design_tabular_q_learning.services.PlottingSettings;
import domain_design_tabular_q_learning.domain.shared.Tables;

public class RoadAgentPlotter<V,A> extends AgentPlotter<V,A> {

    EnvironmentRoad environment;
    StateRoad state;

    public RoadAgentPlotter(Agent<V, A> agent,
                            EnvironmentRoad environment,
                            PlottingSettings settings,
                            StateRoad startState) {
        super(agent, settings,new GridSizeExtractor(environment.properties));
        this.environment=environment;
        this.state=startState;
    }

    public Tables createTables() {
        var ep = environment.getProperties();
        var e=super.extractor;
        var tables = new Tables(e);

        for (int y = e.minY(); y <= e.maxY(); y++) {
            for (int x = e.minX(); x <= e.maxX(); x++) {
            //    var state = StateRoad.of(x, y, ep);
                // state.newWithVariables((V) GridVariables.of(x,y));
                int finalY = y;
                int finalX = x;
                Conditionals.executeIfFalse(state.isTerminal(), () ->
                        fillTablesFromState(tables, finalY, finalX, (StateI<V>) state));
            }
        }
        return tables;
    }

    void fillTablesFromState(Tables tables, int y, int x, StateI<V> state) {
        // ActionI<A>[] actionArr = (ActionI<A>[]) environment.actions();
        var actionArr =  environment.actions();
        int nActions = actionArr.length;
        tables.values()[x][y] = String.format(
                settings.tableCellFormatValues(),
                agent.getMemory().valueOfState(state));
        tables.actions()[x][y] = agent.chooseAction(state, 0d).getProperties().toString();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
//        ActionRoad[] actionArr = agent.getProperties().actions();
        var aEnd = actionArr[nActions - 1];
        for (ActionI<GridActionProperties> a : actionArr) {
            StateAction<V,GridActionProperties> sa = StateAction.of(state, a);
            var txt = String.format(settings.tableCellFormatActionValues()
                    , agent.getMemory().read((StateAction<V, A>) sa));
            sb.append(txt);
            Conditionals.executeIfFalse(a.equals(aEnd), () -> sb.append(";"));
        }
        sb.append("]");
        tables.stateActionValues()[x][y] = sb.toString();
    }

}
