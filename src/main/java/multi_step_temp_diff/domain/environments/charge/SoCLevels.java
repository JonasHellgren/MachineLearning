package multi_step_temp_diff.domain.environments.charge;

public record SoCLevels(double socA, double socB) {

    public static SoCLevels of(double socA, double socB) {
        return new SoCLevels(socA,socB);
    }
}
