package multi_step_temp_diff.models;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

@Builder
@ToString
public class NstepExperience {
    @NonNull  public Integer stateToUpdate;
    @NonNull  public Double sumOfRewards;
    @NonNull  public Integer stateToBackupFrom;
}
