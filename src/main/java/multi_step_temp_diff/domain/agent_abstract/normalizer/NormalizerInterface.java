package multi_step_temp_diff.domain.agent_abstract.normalizer;

/***
 *     in ->  net work  -> out
 *
 */

public interface NormalizerInterface {
    double normalize(double out);
    double normalizeInverted(double in);  //in = out from net work, returns non-normalized value

    default boolean isNormalizedValueOk(double value) {
        return Math.abs(value)<1;
    }
}
