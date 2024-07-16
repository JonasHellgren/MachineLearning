package maze_domain_design.domain.agent.value_objects;

import lombok.Builder;
import maze_domain_design.domain.environment.value_objects.Action;
import org.apache.commons.math3.util.Pair;

@Builder
public record AgentProperties(
        Pair<Double,Double> probRandomActionStartEnd,
        double learningRate,
        double defaultValue,
        double gamma,
        Action[] actions
) {

    public static AgentProperties roadMaze() {
        return AgentProperties.builder()
                .probRandomActionStartEnd(Pair.create(0.5,0.01))
                .learningRate(0.1)
                .defaultValue(0)
                .gamma(0.99)
                .actions(Action.values())
                .build();
    }
}
