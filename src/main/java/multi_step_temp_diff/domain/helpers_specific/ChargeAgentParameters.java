package multi_step_temp_diff.domain.helpers_specific;

import multi_step_temp_diff.domain.agent_parts.neural_memory.normalizer.NormalizerMeanStd;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ChargeAgentParameters {

    public static final int NOF_STEPS_BETWEEN_UPDATED_AND_BACKUPED = 5;
    public static final int NOF_EPIS = 100;
    public static final int BATCH_SIZE = 100, MAX_BUFFER_SIZE_EXPERIENCE_REPLAY = 100_000;
    public static final int MAX_NOF_STEPS_TRAINING = 100;

    public static final Pair<Double, Double> START_END_PROB = Pair.of(0.5, 1e-5);
    public static final double LEARNING_RATE = 0.01;
    public static final int NOF_LAYERS = 5;
    public static final int NOF_NEURONS_HIDDEN = 30;
    public static final double DISCOUNT_FACTOR = 0.99;
    public static final double MOMENTUM = 0.01d;

    public static final double VALUE_IF_NOT_OCCUPIED = 1.1d;
    public static final NormalizerMeanStd NORMALIZER_CHARGE_INPUT_ONEDOTONE =
            new NormalizerMeanStd(List.of(0.3, 0.5, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d, 1.1d));
    public static final int TRAP_POS = 29; //trap
    public static final List<Double> CHARGE_REWARD_VALUES_EXCEPT_FAIL = List.of(0d, -1d, -2d, 0d, -1d, 0d);
    public static final double ALPHA = 3d;  //used by out normalizer
    public static final String FOLDER_NETWORKS = "networks/";
    public static final String FILENAME_CHARGE_BOTH_FREE_NET = "chargeBothFreeNet.nnet";
    public static final int BUFFER_SIZE_FOR_RESET = 1000;
    public static final int TIME_BUDGET_RESET = 0;  //small <=> no memory reset
}
