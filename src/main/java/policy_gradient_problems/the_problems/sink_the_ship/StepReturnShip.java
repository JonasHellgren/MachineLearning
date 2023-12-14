package policy_gradient_problems.the_problems.sink_the_ship;

public record StepReturnShip(int state, boolean isTerminal, boolean isHit, double reward) {
}