package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.Optional;

@Builder
@ToString
public class NstepExperience {

    public static Integer STATE_IF_NOT_PRESENT=-1;
    @NonNull  public Integer stateToUpdate;
    @NonNull  public Double sumOfRewards;
    @NonNull  public Integer stateToBackupFrom;
    @NonNull public boolean isBackupStatePresent;
}
