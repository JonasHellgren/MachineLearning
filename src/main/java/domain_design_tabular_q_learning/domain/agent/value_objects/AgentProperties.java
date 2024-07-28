package domain_design_tabular_q_learning.domain.agent.value_objects;

import lombok.Builder;
import lombok.With;
import org.apache.commons.math3.util.Pair;

@Builder
public record AgentProperties(
        @With Pair<Double,Double> probRandomActionStartEnd,
        @With double learningRate,
        double defaultValue,
        @With  double gamma
       // ActionRoad[] actions
) {

    public static AgentProperties roadMaze() {
        return AgentProperties.builder()
                .probRandomActionStartEnd(Pair.create(0.5,0.0)) //0.5,0.0
                .learningRate(0.9) //0.9,0.2 (std non zero)
                .defaultValue(0)
                .gamma(1.0)
                .build();
    }

    public static AgentProperties tunnels() {
        return roadMaze();
    }

    public double probRandomAction(double progress) {
        Double p0 = probRandomActionStartEnd.getFirst();
        Double p1 = probRandomActionStartEnd.getSecond();
        return p0+progress*(p1-p0);
    }
}
