package domain_design_tabular_q_learning.environments.tunnels;

public record TunnelActionProperties(
        int deltaY,
        String arrow
) {

    public static TunnelActionProperties of(int deltaY, String arrow) {
        return new TunnelActionProperties(deltaY,arrow);
    }

}
