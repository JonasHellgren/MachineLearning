package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;

@Builder
@ToString
public class NstepExperience {

    private static final double VALUE_NOT_DEF = -1d;
    private static final boolean BACKUP_STATE_PRESENT = false;
    private static final double SUM_R = 0d;
    public static Integer STATE_IF_NOT_PRESENT=-1;

    @NonNull  public Integer stateToUpdate;
    @Builder.Default
    public Double sumOfRewards= SUM_R;
    @Builder.Default
    public Integer stateToBackupFrom=STATE_IF_NOT_PRESENT;
    @Builder.Default
    public boolean isBackupStatePresent= BACKUP_STATE_PRESENT;
    @Builder.Default
    public Double value= VALUE_NOT_DEF;
}
