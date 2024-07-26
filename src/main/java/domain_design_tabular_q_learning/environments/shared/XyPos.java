package domain_design_tabular_q_learning.environments.shared;

public record XyPos(Integer x, Integer y) {

    public static XyPos of(Integer x, Integer y) {
        return new XyPos(x,y);
    }
}
