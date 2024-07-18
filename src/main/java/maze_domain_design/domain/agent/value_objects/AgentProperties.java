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
                .probRandomActionStartEnd(Pair.create(0.5,0.0))
                .learningRate(0.1)
                .defaultValue(0)
                .gamma(1.0)
                .actions(Action.values())
                .build();
    }

    public double probRandomAction(double progress) {
        Double p0 = probRandomActionStartEnd.getFirst();
        Double p1 = probRandomActionStartEnd.getSecond();
        return p0 +progress*(p1-p0);
    }
}
