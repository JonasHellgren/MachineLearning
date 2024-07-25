package domain_design_tabular_q_learning.environments.avoid_obstacle;

public record GridActionProperties(
        int deltaY,
        String arrow
) {

    public static GridActionProperties of(int deltaY,String arrow) {
        return new GridActionProperties(deltaY,arrow);
    }

}
