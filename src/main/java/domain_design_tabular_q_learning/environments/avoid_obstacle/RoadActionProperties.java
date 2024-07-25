package domain_design_tabular_q_learning.environments.avoid_obstacle;

public record RoadActionProperties(
        int deltaY,
        String arrow
) {

    public static RoadActionProperties of(int deltaY, String arrow) {
        return new RoadActionProperties(deltaY,arrow);
    }

}
