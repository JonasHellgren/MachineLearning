package policy_gradient_zeroOrOne.domain;

public record Experience(int action, double reward, double value) {
}
