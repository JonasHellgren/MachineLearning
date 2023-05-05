package multi_step_temp_diff.models;


import lombok.Builder;

@Builder
public class StepReturn {
    public int state;
    public double reward;
    public boolean isNewStateTerminal;
}
