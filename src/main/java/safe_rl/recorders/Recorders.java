package safe_rl.recorders;

import lombok.Getter;
import policy_gradient_problems.helpers.RecorderStateValues;
import policy_gradient_problems.helpers.RecorderTrainingProgress;

public class Recorders {

    public final RecorderStateValues recorderStateValues=new RecorderStateValues();
    public final RecorderTrainingProgress recorderTrainingProgress =new RecorderTrainingProgress();
}
