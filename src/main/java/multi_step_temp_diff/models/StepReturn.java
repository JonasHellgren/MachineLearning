package multi_step_temp_diff.models;
import lombok.Builder;
import lombok.ToString;
import multi_step_temp_diff.domain.interfaces_and_abstract.StateInterface;

@Builder
@ToString
public class StepReturn<S> {
    public StateInterface<S> newState;
    public double reward;
    public boolean isNewStateTerminal;
    @Builder.Default
    public boolean isNewStateFail=false;
}
