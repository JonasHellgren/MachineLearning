package policy_gradient_problems.environments.sink_the_ship;

public record VariablesShip(int pos) {

    public static VariablesShip newDefault() {
        return new VariablesShip(0);
    }

    public VariablesShip copy() {
        return new VariablesShip(pos);
    }
}
