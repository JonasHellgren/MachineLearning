package multi_step_temp_diff.domain.agent_abstract.normalizer;

public class NormalizeMinMax implements NormalizerInterface {

    double min, max;

    public NormalizeMinMax(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public double normalize(double out) {
        return (out-min)/(max-min);
    }

    @Override
    public double normalizeInverted(double in) {
        return min+in*(max-min);
    }
}
