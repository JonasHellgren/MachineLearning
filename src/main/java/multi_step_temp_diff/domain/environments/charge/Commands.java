package multi_step_temp_diff.domain.environments.charge;

public record Commands(int cA, int cB) {

    public static Commands of(int cA, int cB) {
        return new Commands(cA,cB);
    }
}
