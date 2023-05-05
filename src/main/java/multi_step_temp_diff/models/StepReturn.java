package multi_step_temp_diff.models;


import lombok.Builder;

@Builder
public class StepReturn {
    public int newState;
    public double reward;
    public boolean isNewStateTerminal;
}
