package multi_step_temp_diff.domain.environment_abstract;
import lombok.Builder;
import lombok.ToString;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;

@Builder
@ToString
public class StepReturn<S> {
    public StateInterface<S> newState;
    public double reward;
    public boolean isNewStateTerminal;
    @Builder.Default
    public boolean isNewStateFail=false;
}
