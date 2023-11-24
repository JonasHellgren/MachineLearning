package policy_gradient_problems.common;

public record Experience(int state, int action, double reward, int stateNext,  double value) {
}
