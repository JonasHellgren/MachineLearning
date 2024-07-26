package domain_design_tabular_q_learning.environments.tunnels;

import domain_design_tabular_q_learning.domain.environment.value_objects.ActionI;
import domain_design_tabular_q_learning.environments.avoid_obstacle.RoadActionProperties;
import lombok.Getter;

public enum ActionTunnel implements ActionI<TunnelActionProperties> {
    N(TunnelActionProperties.of(0,1,"↑")),
    E(TunnelActionProperties.of(1,0,"→")),
    S(TunnelActionProperties.of(0,-1,"↓")),
    W(TunnelActionProperties.of(-1,0,"←"));

    @Getter  final TunnelActionProperties properties;

    ActionTunnel(TunnelActionProperties properties) {
        this.properties=properties;
    }

    @Override
    public String toString() {
        return properties.arrow();
    }


}
