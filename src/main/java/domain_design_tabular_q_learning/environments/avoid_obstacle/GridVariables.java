package domain_design_tabular_q_learning.environments.avoid_obstacle;

public record GridVariables(Integer x, Integer y) {

    public static GridVariables of(Integer x, Integer y) {
        return new GridVariables(x,y);
    }
}
