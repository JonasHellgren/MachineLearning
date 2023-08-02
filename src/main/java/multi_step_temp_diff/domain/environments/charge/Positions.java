package multi_step_temp_diff.domain.environments.charge;

public record Positions(int posA, int posB) {
    public static Positions of(int posA, int posB) {
        return new Positions(posA,posB);
    }
}
