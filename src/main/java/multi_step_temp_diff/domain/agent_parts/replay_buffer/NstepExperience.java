package multi_step_temp_diff.domain.agent_parts.replay_buffer;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;

@Builder
@ToString
public class NstepExperience<S> {

    private static final double VALUE_NOT_DEF = -1d;
    private static final boolean BACKUP_STATE_PRESENT = false;
    private static final double SUM_R = 0d;
    public static final double INIT_TD_ERROR = 100d;
    public static final double INIT_PRIORITIZATION = 1d;
    public static final double INIT_WEIGHT = 1d;
    public static final double INIT_PROB = 0d;
    // public static StateInterface<S> STATE_IF_NOT_PRESENT=-1;

    @NonNull  public StateInterface<S> stateToUpdate;
    @Builder.Default
    public Double sumOfRewards= SUM_R;
    @Builder.Default
    public StateInterface<S> stateToBackupFrom=null; //TODO optional?
    @Builder.Default
    public boolean isBackupStatePresent= BACKUP_STATE_PRESENT;
    @Builder.Default
    public Double value = VALUE_NOT_DEF;
    @Builder.Default
    public Double tdError = INIT_TD_ERROR;
    @Builder.Default
    public Double prioritization = INIT_PRIORITIZATION;
    @Builder.Default
    public Double probability = INIT_PROB;
    @Builder.Default
    public Double weight = INIT_WEIGHT;
}
