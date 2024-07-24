package domain_design_tabular_q_learning.environments.obstacle_on_road;

public record GridActionProperties(
        int deltaY,
        String arrow
) {

    public static GridActionProperties of(int deltaY,String arrow) {
        return new GridActionProperties(deltaY,arrow);
    }

}
