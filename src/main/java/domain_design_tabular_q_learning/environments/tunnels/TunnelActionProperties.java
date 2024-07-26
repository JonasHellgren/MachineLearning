package domain_design_tabular_q_learning.environments.tunnels;

public record TunnelActionProperties(
        int deltaX,
        int deltaY,
        String arrow
) {

    public static TunnelActionProperties of(int deltaX, int deltaY, String arrow) {
        return new TunnelActionProperties(deltaX,deltaY,arrow);
    }

}
